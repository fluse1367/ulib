package eu.software4you.connection.javaserver.server;


import eu.software4you.connection.javaserver.request.RequestManager;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class Server extends Thread {
    private final InetAddress listen;
    private final Logger logger;
    private final RequestManager requestManager;
    public int port;
    public SSLServerSocket serverSocket;
    public ArrayList<Connection> connections = new ArrayList<>();
    public boolean down = false;

    public Server(InetAddress listen, int port, Logger logger) {
        this.listen = listen;
        this.port = port;
        this.logger = logger;
        RequestManager requestManager = null;
        try {
            Constructor<RequestManager> constructor = RequestManager.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            requestManager = constructor.newInstance();
        } catch (Exception e) {
        }
        this.requestManager = requestManager;
    }

    public final void listen() throws IOException {
        serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(this.port, 0, this.listen);

        logger.info("Listening " + serverSocket);
        logger.info("Supported Cipher Suites: " + Arrays.toString(((SSLServerSocketFactory) SSLServerSocketFactory.getDefault()).getSupportedCipherSuites()));
        start();
    }

    public Logger getLogger() {
        return logger;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public SSLServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Connection conn = new Connection((SSLSocket) serverSocket.accept(), this);
                if (!isInterrupted()) {
                    connections.add(conn);
                    conn.start();
                }
            } catch (Exception e) {
                if (!e.getMessage().toLowerCase().contains("closed"))
                    e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        down = true;
        interrupt();
        try {
            if (serverSocket != null)
                if (!serverSocket.isClosed())
                    serverSocket.close();
                else
                    logger.warning("Listener already closed.");
            for (Connection connection : connections) {
                connection.client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
