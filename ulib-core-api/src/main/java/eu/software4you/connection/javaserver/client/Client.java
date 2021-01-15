package eu.software4you.connection.javaserver.client;

import eu.software4you.connection.javaserver.request.RequestManager;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Constructor;

public class Client extends Thread {
    private final String host;
    private final int port;
    private final RequestManager requestManager;
    private Connection connection;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        RequestManager requestManager = null;
        try {
            Constructor<RequestManager> constructor = RequestManager.class.getConstructor();
            constructor.setAccessible(true);
            requestManager = constructor.newInstance();
        } catch (Exception e) {
        }
        this.requestManager = requestManager;
    }

    public void connect() {
        try {
            connection = new Connection((SSLSocket) SSLSocketFactory.getDefault().createSocket(this.host, this.port), this);
            connection.start();
        } catch (Exception e) {
            if (!e.getMessage().equalsIgnoreCase("connection refused: connect")) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        interrupt();
        try {
            if (connection != null) {
                connection.interrupt();
                if (connection.socket != null)
                    connection.socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }
}
