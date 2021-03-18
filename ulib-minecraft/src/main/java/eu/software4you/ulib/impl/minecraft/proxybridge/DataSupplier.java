package eu.software4you.ulib.impl.minecraft.proxybridge;

import eu.software4you.ulib.ULib;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class DataSupplier implements Supplier<byte[]> {
    private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private final long timeout;

    public DataSupplier(long timeout) {
        this.timeout = timeout;
    }

    public void supply(byte[] data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            ULib.get().exception(e, "Cannot supply data in SBB");
        }
    }

    @Override
    public byte[] get() {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ULib.get().exception(e, "Cannot pull data from SBB");
        }
        return null;
    }
}
