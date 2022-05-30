package eu.software4you.ulib.core.inject;

import org.jetbrains.annotations.NotNull;

/**
 * An exception indicating that not all configured injections could get completed.
 */
public class ConfigurationSatisfactionException extends Exception {
    public ConfigurationSatisfactionException(@NotNull String message) {
        super(message);
    }
}
