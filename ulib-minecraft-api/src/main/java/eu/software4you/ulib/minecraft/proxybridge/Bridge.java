package eu.software4you.ulib.minecraft.proxybridge;

import java.util.concurrent.Future;

public interface Bridge {
    String CHANNEL = "ulib:sbb";
    String PROXY_SERVER_NAME = ":bungeecord:";

    /**
     * Requests data from a specific server. Use {@link #PROXY_SERVER_NAME} to send the request to the proxy itself.
     *
     * @param targetServer the server to send the request to
     * @param line         the request "command" line
     * @param timeout      the maximum time to wait for the answer in milliseconds, -1 for infinite waiting
     * @return a {@link Future} representing the action
     * @throws IllegalArgumentException if the targetServer was not found
     * @throws IllegalArgumentException if the sender and the target is the same server
     */
    Future<byte[]> request(String targetServer, String line, long timeout);

    /**
     * Requests data from a specific server. Use {@link #PROXY_SERVER_NAME} to send the request to the proxy itself.
     *
     * @param targetServer the server to send the request to
     * @param line         the request "command" line
     * @return a {@link Future} representing the action
     * @throws IllegalArgumentException if the targetServer was not found
     */
    default Future<byte[]> request(String targetServer, String line) {
        return request(targetServer, line, -1);
    }

    /**
     * Requests data from the server the last request was received from.
     *
     * @param line    the request "command" line
     * @param timeout the maximum time to wait for the answer in milliseconds, -1 for infinite waiting
     * @return a {@link Future} representing the action
     * @throws IllegalStateException if no command was ever received and thus no server to send the command to is known
     */
    Future<byte[]> request(String line, long timeout);

    /**
     * Requests data from the server the last request was received from.
     *
     * @param line the request "command" line
     * @return a {@link Future} representing the action
     * @throws IllegalStateException if no command was ever received and thus no server to send the command to is known
     */
    default Future<byte[]> request(String line) {
        return request(line, -1);
    }

    /**
     * Sends a command to a specific server. Use {@link #PROXY_SERVER_NAME} to send the request to the proxy itself.
     * Does not wait for an answer as this method is not designed for answers.
     *
     * @param targetServer the server to send the command to
     * @param line         the command line
     * @throws IllegalArgumentException if the targetServer was not found
     * @throws IllegalArgumentException if the sender and the target is the same server
     */
    void trigger(String targetServer, String line);

    /**
     * Sends a command to the server the last command was received from.
     *
     * @param line the command line
     * @throws IllegalStateException if no command was ever received and thus no server to send the command to is known
     */
    void trigger(String line);
}
