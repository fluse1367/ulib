package eu.software4you.connection.javaserver.request;

public class Request {
    private final String request;
    private final String[] arguments;

    Request(String request, String[] arguments) {
        this.request = request;
        this.arguments = arguments;
    }

    public String getRequest() {
        return request;
    }

    public String[] getArguments() {
        return arguments;
    }
}
