package eu.software4you.ulib.inject;

import java.lang.reflect.Field;

public class InjectionException extends RuntimeException {

    public InjectionException(Object object, Class<?> target, String message) {
        this(object.getClass(), target, message);
    }

    public InjectionException(Object object, Field target, String message) {
        this(object.getClass(), target, message);
    }

    public InjectionException(Class<?> object, Class<?> target, String message) {
        this(object.toString(), target.toString(), message);
    }

    public InjectionException(Class<?> object, Field target, String message) {
        this(object.toString(), target.toString(), message);
    }

    private InjectionException(String object, String target, String message) {
        super(String.format("Cannot inject %s into %s: %s", object, target, message));
    }

    public InjectionException(Object object, Class<?> target, String message, Throwable cause) {
        this(object.getClass(), target, message, cause);
    }

    public InjectionException(Object object, Field target, String message, Throwable cause) {
        this(object.getClass(), target, message, cause);
    }

    public InjectionException(Class<?> object, Class<?> target, String message, Throwable cause) {
        this(object.toString(), target.toString(), message, cause);
    }

    public InjectionException(Class<?> object, Field target, String message, Throwable cause) {
        this(object.toString(), target.toString(), message, cause);
    }

    private InjectionException(String object, String target, String message, Throwable cause) {
        super(String.format("Cannot inject %s into %s: %s", object, target, message), cause);
    }
}
