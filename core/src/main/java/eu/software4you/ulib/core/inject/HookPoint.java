package eu.software4you.ulib.core.inject;

/**
 * Represents the location of a method where an injection is acceptable.
 */
public enum HookPoint {
    /**
     * Directly at the start of a method before any code is executed. This hook point is intercepting.
     */
    HEAD,

    /**
     * At any return statement. This hook point is intercepting.
     */
    RETURN,

    /**
     * At a method call. This hook point is proxying.
     */
    METHOD_CALL,

    /**
     * At a read-access of a field. This hook point is proxying.
     */
    FIELD_READ,

    /**
     * At a write-access of a field. This hook point is proxying.
     */
    FIELD_WRITE,
}
