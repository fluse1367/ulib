package eu.software4you.litetransform;

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
}
