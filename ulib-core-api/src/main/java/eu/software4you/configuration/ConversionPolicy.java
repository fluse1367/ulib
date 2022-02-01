package eu.software4you.configuration;

/**
 * Policy how to behave when a value cannot be converted to the requested type.
 */
public enum ConversionPolicy {
    /**
     * Returns the default value
     */
    RETURN_DEFAULT,

    /**
     * Throws an exception
     */
    THROW_EXCEPTION;
}
