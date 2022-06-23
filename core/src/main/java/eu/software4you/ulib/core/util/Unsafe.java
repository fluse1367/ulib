package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.function.Task;
import eu.software4you.ulib.core.impl.BypassAnnotationEnforcement;
import eu.software4you.ulib.core.impl.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * A class for accessing unsafe operations.
 * <p>
 * Use it with caution and only (I mean it!) if you know what you're doing!
 *
 * @deprecated Well... its unsafe...
 */
@Deprecated
public final class Unsafe {

    /**
     * Executes a given task in the current thread with effectively no access control on reflective operations.
     * <p>
     * The access control on reflective objects is disabled for the current thread while executing this method.
     *
     * @param task the task to execute
     */
    public static <X extends Exception> void doPrivileged(@NotNull Task<X> task) throws X {
        doPrivileged((Func<Void, X>) () -> {
            task.execute();
            return null;
        });
    }

    /**
     * Executes a given task in the current thread with effectively no access control on reflective operations.
     * <p>
     * The access control on reflective objects is disabled for the current thread while executing this method.
     *
     * @param task the task to execute
     */
    @BypassAnnotationEnforcement
    public static <T, X extends Exception> T doPrivileged(@NotNull Func<T, X> task) throws X {
        return Internal.sudo(task);
    }

}
