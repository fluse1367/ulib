package eu.software4you.ulib.core.configuration.serialization;

import org.jetbrains.annotations.NotNull;

public class InvalidFactoryDeclarationException extends SerializationException {
    public InvalidFactoryDeclarationException(@NotNull String message) {
        super(message);
    }
}
