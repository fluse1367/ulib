package eu.software4you.connection.javaserver.request;

import eu.software4you.connection.IConnection;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

public class RequestManager {
    private final HashMap<String, RequestExecutor> executors = new HashMap<>();

    RequestManager() {
    }

    public void registerRequestExecutor(RequestExecutor executor, String request) {
        if (executors.containsKey(request))
            throw new IllegalArgumentException("Request already registered!");
        executors.put(request, executor);
    }

    public String execute(IConnection sender, Request request) {
        Validate.notNull(sender);
        Validate.notNull(request);
        if (executors.containsKey(request.getRequest()))
            return executors.get(request.getRequest()).onRequest(sender, request);
        return null;
    }
}
