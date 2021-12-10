package eu.software4you.ulib.core.api.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MinecraftProtocolOutputStream extends DataOutputStream {
    public MinecraftProtocolOutputStream(OutputStream out) {
        super(out);
    }

    public void writeVarInt(int value) throws IOException {
        do {
            byte currentByte = (byte) (value & 0b01111111);

            value >>>= 7;
            if (value != 0) currentByte |= 0b10000000;

            writeByte(currentByte);
        } while (value != 0);
    }

    public void writeVarLong(long value) throws IOException {
        do {
            byte currentByte = (byte) (value & 0b01111111);

            value >>>= 7;
            if (value != 0) currentByte |= 0b10000000;

            writeByte(currentByte);
        } while (value != 0);
    }

    public void writeString(String string) throws IOException {
        byte[] buf = string.getBytes();
        writeVarInt(buf.length);
        write(buf);
    }

    public void writeUUID(UUID uuid) throws IOException {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }


    public void writeBlockPosition(int x, int y, int z) throws IOException {
        writeLong(((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF));
    }
}
