package eu.software4you.io;

import eu.software4you.common.collection.Triple;

import java.io.*;
import java.util.UUID;

public class MinecraftProtocolInputStream extends DataInputStream {
    public MinecraftProtocolInputStream(InputStream in) {
        super(in);
    }

    public static MinecraftProtocolInputStream wrap(byte[] buffer) {
        return new MinecraftProtocolInputStream(new ByteArrayInputStream(buffer));
    }

    public int readVarInt() throws IOException {
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
    }

    public long readVarLong() throws IOException {
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
    }

    public String readString() throws IOException {
        int len = readVarInt();
        if (len <= -1)
            throw new EOFException();

        byte[] buf = new byte[len];
        readFully(buf);
        return new String(buf);
    }

    public UUID readUUID() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public Triple<Integer, Integer, Integer> readBlockPosition() throws IOException {
        var val = readLong();
        int x = (int) (val >> 38);
        int y = (int) (val & 0xFFF);
        int z = (int) (val << 26 >> 38);
        return new Triple<>(x, y, z);
    }
}
