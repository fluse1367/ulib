package eu.software4you.connection.javaserver.server;


import eu.software4you.connection.IServer;
import eu.software4you.connection.javaserver.request.RequestParser;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Logger;

public class Connection extends Thread implements IServer {

    public final SSLSocket client;
    public final PrintStream out;
    final String threadName;
    final Logger logger;
    final BufferedReader in;
    private final Server parentServer;
    SSLSession session;

    Connection(SSLSocket client, Server parentServer) {
        this.client = client;
        this.parentServer = parentServer;
        this.threadName = this.getName();
        this.logger = parentServer.getLogger();

        BufferedReader in = null;
        PrintStream out = null;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.out = out;
        this.in = in;

        logger.info(this.client.getInetAddress().getHostAddress() + "/" + this.client.getPort() + " connected");
    }

    @Override
    public void run() {
        try {
            //session = client.getSession();
            //String cipherSuite = session.getCipherSuite();
            //Logger.info(threadName+": Using cipher suite "+cipherSuite);

            try {
                String line;
                while (!client.isClosed() && client.isConnected() && !isInterrupted() && (line = in.readLine()) != null) {
                    parentServer.getRequestManager().execute(this, RequestParser.parseRequest(line));
                }
            } catch (Exception ignored) {

            } finally {
                in.close();
                out.close();
                if (!client.isClosed())
                    client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.info(this.client.getInetAddress().getHostAddress() + "/" + this.client.getPort() + " disconnected");
        }
    }

    public boolean isClosed() {
        return client.isClosed();
    }

    public void close() throws IOException {
        client.close();
    }

    public void send(String msg) {
        out.println(msg);
    }
}
