package eu.software4you.ulib.minecraft.io;

import eu.software4you.ulib.core.collection.Triple;
import eu.software4you.ulib.core.function.*;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Minecraft protocol operations.
 *
 * @see <a href="https://wiki.vg/Protocol">Minecraft Protocol Specification</a>
 */
public final class MinecraftIO {

    /**
     * The bit in a varint/varlong indicating more data.
     * This is the 8th bit (0b10000000/0x80).
     */
    private static final int VARVAL_INDICATOR_BIT = 0x80;
    /**
     * The offset amount for varint/varlong decoding (7 because the 8th bit is the indicator).
     */
    private static final int VARVAL_OFFSET = 7;

    /**
     * Decodes a variable length number.
     *
     * @param maxBlocks   the maximum of blocks
     * @param init        the initial decoded value (must be 0)
     * @param byteFunc    the byte supplier function
     * @param accumulator the shift function: (value, data, offset): N
     * @param <N>         the number type
     * @return the coded value
     * @throws IOException if a byte cannot be read or the value cannot be decoded
     */
    private static <N extends Number> N readVarNum(
            int maxBlocks, N init,
            Func<Byte, ? extends IOException> byteFunc,
            TriFunction<N, Byte, Integer, N> accumulator) throws IOException {

        if (init == null || init.longValue() != 0)
            throw new IllegalArgumentException("init value");

        N decoded = init;

        for (int block = 0; ; block++) {

            // check for block maximum
            if (!(block < maxBlocks))
                throw new IOException("Malformed varnum (block %d > %d)".formatted(block, maxBlocks));

            // read current byte
            byte _byte = byteFunc.execute();

            // extract data
            byte data = (byte) (_byte & ~VARVAL_INDICATOR_BIT);

            // apply accumulator function (usually shifts data and ORs it with the decoded value)
            decoded = accumulator.apply(decoded, data, block * VARVAL_OFFSET);

            // extract indicator bit and check if it is set
            if ((_byte & VARVAL_INDICATOR_BIT) == 0)
                return decoded; // bit is NOT set, so we're done
            // bit is set, continue
        }

        // unreachable
    }

    /**
     * Encodes a variable length number.
     *
     * @param maxBlocks the maximum of blocks
     * @param consumer  the byte consumer function
     * @param shifter   the shift function: (value, offset): byte
     * @param <N>       the number type
     * @throws IOException if a byte cannot be written
     */
    private static <N extends Number> void writeVarNum(
            int maxBlocks, N value,
            ParamTask<Byte, ? extends IOException> consumer,
            BiFunction<N, Integer, N> shifter) throws IOException {

        for (int block = 0; ; block++) {

            // check for block maximum
            if (!(block < maxBlocks))
                throw new InternalError(); // shouldn't happen

            // shift
            N shifted = shifter.apply(value, block * VARVAL_OFFSET);

            // encode into block format
            byte data = (byte) (shifted.byteValue() & ~VARVAL_INDICATOR_BIT);

            // check if there's a next block
            long left = (shifted.longValue() & ~data);
            if (left == 0) {
                // there's no next block
                // just write pre-computed block value as it doesn't contain the indicator bit
                consumer.execute(data);
                return; // done writing varnum
            }

            // there's a next block
            // write block value with indicator bit set
            consumer.execute((byte) (data | VARVAL_INDICATOR_BIT));
        }

        // unreachable
    }


    /**
     * Reads a variable length 32-bit integer from a data input.
     *
     * @param in the data input to read from
     * @return the decoded value
     */
    @NotNull
    public static Expect<Integer, IOException> readVarInt(@NotNull DataInput in) {
        return Expect.compute(() -> readVarNum(5, 0, in::readByte,
                (decoded, data, off) -> decoded | Byte.toUnsignedInt(data) << off)
        );
    }

    /**
     * Writes a variable length 32-bit integer to a data output.
     *
     * @param out   the data output to write to
     * @param value the value to write
     */
    @NotNull
    public static Expect<Void, IOException> writeVarInt(@NotNull DataOutput out, final int value) {
        return Expect.compute(() -> writeVarNum(5, value,
                b -> out.writeByte(Byte.toUnsignedInt(b)),
                (val, offset) -> val >>> Math.min(offset, 31))
        );
    }

    /**
     * Reads a variable length 64-bit integer from a data input.
     *
     * @param in the data input to read from
     * @return the decoded value
     */
    @NotNull
    public static Expect<Long, IOException> readVarLong(@NotNull DataInput in) {
        return Expect.compute(() -> readVarNum(10, 0L, in::readByte,
                (decoded, data, off) -> decoded | Byte.toUnsignedLong(data) << off)
        );
    }

    /**
     * Writes a variable length 64-bit integer to a data output.
     *
     * @param out   the data output to write to
     * @param value the value to write
     */
    @NotNull
    public static Expect<Void, IOException> writeVarLong(@NotNull DataOutput out, final long value) {
        return Expect.compute(() -> writeVarNum(10, value,
                b -> out.writeByte(Byte.toUnsignedInt(b)),
                (val, offset) -> val >>> Math.min(offset, 63))
        );
    }


    /**
     * Reads a string from a data input.
     *
     * @param in the data input to read from
     * @return the string
     */
    @NotNull
    public static Expect<String, IOException> readString(@NotNull DataInput in) {
        return Expect.compute(() -> {
            var len = readVarInt(in)
                    .orElseRethrow(IOException.class);
            if (len < 0)
                throw new EOFException();
            byte[] buf = new byte[len];
            in.readFully(buf);
            return new String(buf);
        });
    }

    /**
     * Writes a string to a data output.
     *
     * @param out   the data output to write to
     * @param value the value to write
     */
    @NotNull
    public static Expect<Void, IOException> writeString(@NotNull DataOutput out, @NotNull String value) {
        return Expect.compute(() -> {
            var buf = value.getBytes();
            writeVarInt(out, buf.length)
                    .rethrow(IOException.class);
            out.write(buf);
        });
    }

    /**
     * Reads a UUID from a data input.
     *
     * @param in the data input to read from
     * @return the uuid
     */
    @NotNull
    public static Expect<UUID, IOException> readUUID(@NotNull DataInput in) {
        return Expect.compute(() -> new UUID(in.readLong(), in.readLong()));
    }

    /**
     * Writes a UUID to a data output.
     *
     * @param out   the data output to write to
     * @param value the value to write
     */
    @NotNull
    public static Expect<Void, IOException> writeUUID(@NotNull DataOutput out, @NotNull UUID value) {
        return Expect.compute(() -> {
            out.writeLong(value.getMostSignificantBits());
            out.writeLong(value.getLeastSignificantBits());
        });
    }

    /**
     * Reads an integer block position (x,y,z) from a data input.
     *
     * @param in the data input to read from
     * @return the block position boxed in a {@link Triple}
     */
    @NotNull
    public static Expect<Triple<Integer, Integer, Integer>, IOException> readBlockPosition(@NotNull DataInput in) {
        /*
            The block position is a 64-bit integer split into 3 parts:
            <X: 26 bits><Z: 26 bits><Y: 12 bits>
         */

        return Expect.compute(() -> {
            var val = in.readLong();

            // cut to 12 bits
            int y = (int) (val & 0xFFF);

            // shift right by 12 bits and cut to 26 bits
            // (last 12 bits occupied by Y)
            int z = (int) ((val >>> 12) & 0x3FFFFFF);

            // shift right by 38 bits and cut to 26 bits
            // (last 12+26 bits occupied by Y and Z)
            int x = (int) ((val >>> 12 + 26) & 0x3FFFFFF);

            return new Triple<>(x, y, z);
        });
    }

    /**
     * Writes an integer block position (x,y,z) to a data output.
     *
     * @param out the data output to write to
     */
    @NotNull
    public static Expect<Void, IOException> writeBlockPosition(@NotNull DataOutput out, int x, int y, int z) {
        return Expect.compute(() -> {
            // cut to 12 bits
            int y_ = y & 0xFFF;

            // cut to 26 bits and shift left by 12 bits
            // (last 12 bits occupied by Y)
            int z_ = (z & 0x3FFFFFF) << 12;

            // cut to 26 bits and shift left by 38 bits
            // (last 12+26 bits occupied by Y and Z)
            long x_ = (long) (x & 0x3FFFFFF) << 12 + 26;

            out.writeLong(x_ | z_ | y_);
        });
    }


}
