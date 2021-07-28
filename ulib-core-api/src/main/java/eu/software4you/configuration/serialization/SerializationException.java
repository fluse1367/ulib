package eu.software4you.configuration.serialization;

public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
