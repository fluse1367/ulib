package eu.software4you.ulib.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Convenient class for handling singleton instances set by external sources.
 * <p>
 * This class is meant to be declared as public static final field inside the singleton target class:
 * <pre>
 * {@code
 *  public class MyClass {
 *      public static final SingletonInstance<MyClass> INSTANCE = new SingletonInstance<>();
 *      // ...
 *  }
 * }</pre>
 *
 * @param <T> singleton type
 */
public final class SingletonInstance<T> {
    private T instance;

    /**
     * @return an optional wrapping the instance
     */
    @NotNull
    public Optional<T> getInstance() {
        return Optional.ofNullable(instance);
    }

    /**
     * @return the instance
     */
    @Nullable
    public T getUnsafe() {
        return instance;
    }

    /**
     * returns the instance, throwing an exception if the instance is not set
     *
     * @return the instance
     * @throws IllegalStateException if the instance has not been set yet
     */
    @NotNull
    public T get() {
        if (!isSet())
            throw new IllegalStateException("Instance has not been set yet");
        return instance;
    }

    /**
     * @return {@code true} if the instance is set, {@code false} otherwise
     */
    public boolean isSet() {
        return instance != null;
    }

    /**
     * Sets the instance.
     *
     * @param instance the instance to set
     * @throws IllegalStateException if the instance has already been set
     */
    public void setInstance(T instance) {
        if (isSet())
            throw new IllegalStateException("Instance already set");
        this.instance = instance;
    }
}
