package eu.software4you.ulib.core.inject;

import lombok.experimental.StandardException;

/**
 * An exception indicating that not all configured injections could get completed.
 */
@StandardException
public class ConfigurationSatisfactionException extends Exception {
}
