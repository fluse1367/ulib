package eu.software4you.ulib.core.inject;

/**
 * Represents the location of a method where an injection is acceptable.
 */
public enum HookPoint {
    /**
     * Directly at the start of a method before any code is executed.
     */
    HEAD,

    /**
     * At any return statement.
     */
    RETURN,

    /**
     * At a method call.
     */
    METHOD_CALL,

    /**
     * At a read-access of a field.
     */
    FIELD_READ,

    /**
     * At a write-access of a field.
     */
    FIELD_WRITE,
}
