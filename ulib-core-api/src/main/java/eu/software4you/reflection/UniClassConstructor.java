package eu.software4you.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class UniClassConstructor extends UniClass {
    private final Constructor<?> constructor;

    /**
     * @param clazz          The target class
     * @param parameterTypes the parameter array
     */
    protected UniClassConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        super(clazz);
        Constructor<?> constructor = null;
        try {
            constructor = getClazz().getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.constructor = constructor;
    }

    /**
     * Uses the constructor represented by this {@code Constructor} object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as necessary.
     *
     * <p>If the number of formal parameters required by the underlying constructor
     * is 0, the supplied {@code initargs} array may be of length 0 or null.
     *
     * <p>If the constructor's declaring class is an inner class in a
     * non-static context, the first argument to the constructor needs
     * to be the enclosing instance; see section 15.9.3 of
     * <cite>The Java&trade; Language Specification</cite>.
     *
     * <p>If the required access and argument checks succeed and the
     * instantiation will proceed, the constructor's declaring class
     * is initialized if it has not already been initialized.
     *
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     *
     * @param initargs array of objects to be passed as arguments to
     *                 the constructor call; values of primitive types are wrapped in
     *                 a wrapper object of the appropriate type (e.g. a {@code float}
     *                 in a {@link Float Float})
     * @return a new object created by calling the constructor
     * this object represents
     */
    public Object newInstance(Object... initargs) {
        try {
            return constructor.newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the actual {@link Constructor} object
     */
    public Constructor<?> raw() {
        return constructor;
    }
}
