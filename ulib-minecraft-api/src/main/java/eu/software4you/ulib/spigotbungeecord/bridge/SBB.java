package eu.software4you.ulib.spigotbungeecord.bridge;

import eu.software4you.ulib.ULib;
import eu.software4you.ulib.spigotbungeecord.bridge.command.CommandManager;

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
public abstract class SBB extends CommandManager implements Bridge {

    private static Bridge instance;
    private final HashMap<UUID, DataSupplier> answers = new HashMap<>();

    /**
     * Returns the current {@link Bridge} instance.
     *
     * @return the current {@link Bridge} instance
     */
    public static Bridge getInstance() {
        return instance;
    }

    /**
     * Sets the current {@link Bridge} instance.
     *
     * @param instance the {@link Bridge} instance to set
     * @throws IllegalStateException if the instance is already set
     */
    public static void setInstance(Bridge instance) {
        if (SBB.instance != null)
            throw new IllegalStateException("Spigot BungeeCord Bridge already initialized");
        SBB.instance = instance;
        ULib.getInstance().debugImplementation("Spigot BungeeCord Bridge");
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
                ULib.getInstance().exception(e, "Cannot supply data in SBB");
            }
        }

        @Override
        public byte[] get() {
            try {
                return queue.poll(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                ULib.getInstance().exception(e, "Cannot pull data from SBB");
            }
            return null;
        }
    }

}
