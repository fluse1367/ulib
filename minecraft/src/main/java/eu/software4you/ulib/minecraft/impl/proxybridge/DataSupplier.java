package eu.software4you.ulib.minecraft.impl.proxybridge;


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
            // TODO: indicate failure?
        }
    }

    @Override
    public byte[] get() {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO: indicate failure
        }
        return null;
    }
}
