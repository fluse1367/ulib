package eu.software4you.ulib.core.api.io;

import eu.software4you.ulib.core.api.common.collection.Triple;
import eu.software4you.ulib.core.api.util.value.Expect;

import java.io.*;
import java.util.UUID;

public class MinecraftProtocolInputStream extends DataInputStream {
    public MinecraftProtocolInputStream(InputStream in) {
        super(in);
    }

    public static MinecraftProtocolInputStream wrap(byte[] buffer) {
        return new MinecraftProtocolInputStream(new ByteArrayInputStream(buffer));
    }

    public Expect<Integer, IOException> readVarInt() {
        return Expect.compute(() -> {
            int decodedInt = 0;
            int bitOffset = 0;
            byte currentByte;
            do {
                currentByte = readByte();
                decodedInt |= (currentByte & 0b01111111) << bitOffset;

                if (bitOffset == 35) throw new RuntimeException("VarInt is too big");

                bitOffset += 7;
            } while ((currentByte & 0b10000000) != 0);

            return decodedInt;
        });
    }

    public Expect<Long, IOException> readVarLong() {
        return Expect.compute(() -> {
            long decodedLong = 0;
            int bitOffset = 0;
            byte currentByte;
            do {
                currentByte = readByte();
                decodedLong |= (long) (currentByte & 0b01111111) << bitOffset;

                if (bitOffset == 70) throw new RuntimeException("VarLong is too big");

                bitOffset += 7;
            } while ((currentByte & 0b10000000) != 0);

            return decodedLong;
        });
    }

    public Expect<String, IOException> readString() {
        return Expect.compute(() -> {
            int len = readVarInt().orElseRethrow();
            if (len <= -1)
                throw new EOFException();

            byte[] buf = new byte[len];
            readFully(buf);
            return new String(buf);
        });
    }

    public Expect<UUID, IOException> readUUID() {
        return Expect.compute(() -> new UUID(readLong(), readLong()));
    }

    public Expect<Triple<Integer, Integer, Integer>, IOException> readBlockPosition() {
        return Expect.compute(() -> {
            var val = readLong();
            int x = (int) (val >> 38);
            int y = (int) (val & 0xFFF);
            int z = (int) (val << 26 >> 38);
            return new Triple<>(x, y, z);
        });
    }
}
