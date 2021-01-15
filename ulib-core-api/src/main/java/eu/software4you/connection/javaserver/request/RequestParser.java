package eu.software4you.connection.javaserver.request;

public class RequestParser {
    public static Request parseRequest(String line) {

        String[] parts = line.split(" ");

        String[] args = new String[parts.length - 1];

        for (int i = 1; i < parts.length; i++)
            args[i - 1] = parts[i];

        return new Request(parts[0], args);
    }
}
