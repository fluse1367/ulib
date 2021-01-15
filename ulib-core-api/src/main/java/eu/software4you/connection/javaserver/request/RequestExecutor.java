package eu.software4you.connection.javaserver.request;

import eu.software4you.connection.IConnection;

public interface RequestExecutor {
    String onRequest(IConnection sender, Request request);
}
