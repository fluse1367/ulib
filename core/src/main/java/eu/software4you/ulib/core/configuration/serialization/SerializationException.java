package eu.software4you.ulib.core.configuration.serialization;

import org.jetbrains.annotations.NotNull;

public class SerializationException extends RuntimeException {
    public SerializationException(@NotNull String message) {
        super(message);
    }

    public SerializationException(@NotNull Throwable cause) {
        super(cause);
    }
}
