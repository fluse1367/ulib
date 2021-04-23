package eu.software4you.litetransform.injection;

/**
 * Represents the location of a method where an injection is acceptable.
 */
public enum InjectionPoint {
    /**
     * Directly at the start of a method before any code is executed.
     */
    HEAD,

    /**
     * Directly at the end of a method after all code was executed.<br>
     * If a method returns before, this injection is not called.
     */
    TAIL,

    /**
     * Directly after the method was called (even if it returned at some point).
     */
    AFTER,

    /**
     * At any return statement.
     */
    RETURN,
}
