package eu.software4you.ulib.core.io;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.UUID;

public class MinecraftProtocolOutputStream extends DataOutputStream {
    public MinecraftProtocolOutputStream(@NotNull OutputStream out) {
        super(out);
    }

    @NotNull
    public Expect<Void, IOException> writeVarInt(final int value) {
        return Expect.compute(() -> {
            int val = value;
            do {
                byte currentByte = (byte) (val & 0b01111111);

                val >>>= 7;
                if (val != 0) currentByte |= 0b10000000;

                writeByte(currentByte);
            } while (val != 0);
        });
    }

    @NotNull
    public Expect<Void, IOException> writeVarLong(final long value) {
        return Expect.compute(() -> {
            long val = value;
            do {
                byte currentByte = (byte) (val & 0b01111111);

                val >>>= 7;
                if (val != 0) currentByte |= 0b10000000;

                writeByte(currentByte);
            } while (val != 0);
        });
    }

    @NotNull
    public Expect<Void, IOException> writeString(@NotNull String string) {
        return Expect.compute(() -> {
            byte[] buf = string.getBytes();
            writeVarInt(buf.length).rethrow(IOException.class);
            write(buf);
        });
    }

    @NotNull
    public Expect<Void, IOException> writeUUID(@NotNull UUID uuid) {
        return Expect.compute(() -> {
            writeLong(uuid.getMostSignificantBits());
            writeLong(uuid.getLeastSignificantBits());
        });
    }

    @NotNull
    public Expect<Void, IOException> writeBlockPosition(int x, int y, int z) {
        return Expect.compute(() ->
                writeLong(((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF)));
    }
}
