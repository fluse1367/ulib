package eu.software4you.ulib.minecraft.proxybridge;

import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.minecraft.proxybridge.command.CommandManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementation and access point of {@link Bridge}.
 */
public abstract class ProxyServerBridge extends CommandManager implements Bridge {

    @Await
    private static ProxyServerBridge impl;
    private final HashMap<UUID, DataSupplier> answers = new HashMap<>();

    /**
     * Returns the current {@link Bridge} instance.
     *
     * @return the current {@link Bridge} instance
     */
    public static Bridge getInstance() {
        return impl;
    }

    protected Future<byte[]> awaitData(UUID id, long timeout) {
        if (timeout == 0) {
            throw new IllegalArgumentException("Illegal timeout: 0");
        }
        DataSupplier supplier = new DataSupplier(timeout);
        answers.put(id, supplier);
        return CompletableFuture.supplyAsync(supplier);
    }

    protected void putData(UUID id, byte[] data) {
        if (!answers.containsKey(id))
            return;
        answers.get(id).supply(data);
    }

    private static class DataSupplier implements Supplier<byte[]> {
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
}
