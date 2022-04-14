package eu.software4you.ulib.minecraft.impl.proxybridge;

import eu.software4you.ulib.core.util.SingletonInstance;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractProxyServerBridge extends eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge {
    public static final SingletonInstance<AbstractProxyServerBridge> INSTANCE = new SingletonInstance<>();

    private final HashMap<UUID, DataSupplier> answers = new HashMap<>();

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
}
