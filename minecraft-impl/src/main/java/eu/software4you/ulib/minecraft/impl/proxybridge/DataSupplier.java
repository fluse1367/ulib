package eu.software4you.ulib.minecraft.impl.proxybridge;


import eu.software4you.ulib.core.ULib;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;

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
            ULib.logger().log(Level.SEVERE, e, () -> "Cannot supply data in SBB");
        }
    }

    @Override
    public byte[] get() {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            ULib.logger().log(Level.SEVERE, e, () -> "Cannot pull data from SBB");
        }
        return null;
    }
}
