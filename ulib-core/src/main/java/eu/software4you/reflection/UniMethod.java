package eu.software4you.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UniMethod {
    private final Method method;

    protected UniMethod(Method method) {
        this.method = method;
        method.setAccessible(true);
    }

    /**
     * @param invoker    the object the underlying method is invoked from. If the Method is static, the invoker should be null.
     * @param parameters the parameters to invoke the Method.
     * @return the return of the method, or null if it throws an exception.
     */
    public Object invoke(Object invoker, Object... parameters) {
        try {
            return method.invoke(invoker, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
        return null;
    }

    /**
     * @return the actual {@link Method} object
     */
    public Method raw() {
        return method;
    }
}
