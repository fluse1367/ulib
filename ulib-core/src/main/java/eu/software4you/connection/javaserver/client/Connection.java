package eu.software4you.connection.javaserver.client;

import eu.software4you.connection.IClient;
import eu.software4you.connection.javaserver.request.RequestParser;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Connection extends Thread implements IClient {
    final SSLSocket socket;
    private final Client client;
    private final String threadName;
    private SSLSession session;
    private BufferedReader in;
    private PrintStream out;

    Connection(SSLSocket socket, Client client) {
        this.socket = socket;
        this.socket.setUseClientMode(true);
        this.client = client;
        threadName = this.getName();
    }

    @Override
    public void run() {
        try {
            session = socket.getSession();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(), true);

            String line;
            while (!isInterrupted() && (line = in.readLine()) != null) {
                client.getRequestManager().execute(this, RequestParser.parseRequest(line));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.disconnect();
        }
    }

    public void send(String msg) {
        out.println(msg);
    }
}
