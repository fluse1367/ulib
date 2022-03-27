package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.function.BiParamFunc;
import eu.software4you.ulib.core.function.ParamFunc;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.*;

import java.util.Arrays;

/**
 * A container for class loading hooks. The hooks may throw exceptions,
 * however in this case the respective method will return {@code null}.
 *
 * @param delegateLoadClass       see {@link ClassLoader#loadClass(String)}
 * @param delegateFindClass       see {@link ClassLoader#findClass(String)}
 * @param delegateFindModuleClass see {@link ClassLoader#findClass(String, String)}
 * @see InjectUtil
 */
@SuppressWarnings("JavadocReference")
public record ClassLoaderDelegation(
        BiParamFunc<String, Boolean, Class<?>, Exception> delegateLoadClass,
        ParamFunc<String, Class<?>, Exception> delegateFindClass,
        BiParamFunc<String, String, Class<?>, Exception> delegateFindModuleClass) {

    /**
     * Constructs a delegation container that forwards any request to the specified class loader.
     *
     * @param delegateTarget the class loader the requests should be forwarded to
     * @return the newly constructed container
     */
    @Contract(pure = true, value = "_ -> new")
    @NotNull
    public static ClassLoaderDelegation delegateToClassLoader(ClassLoader delegateTarget) {
        return new ClassLoaderDelegation(
                (name, resolve) -> ReflectUtil.<Class<?>>call(delegateTarget.getClass(), delegateTarget, "loadClass()",
                        Arrays.asList(Param.from(name), new Param<>(boolean.class, resolve))).getValue(),
                name -> ReflectUtil.<Class<?>>call(delegateTarget.getClass(), delegateTarget, "findClass()",
                        Param.fromMultiple(name)).getValue(),
                (module, name) -> ReflectUtil.<Class<?>>call(delegateTarget.getClass(), delegateTarget, "findClass()",
                        Param.fromMultiple(module, name)).getValue()
        );
    }

    @Nullable
    public Class<?> loadClass(String name, boolean resolve) {
        return Expect.compute(delegateLoadClass, name, resolve).getValue();
    }

    @Nullable
    public Class<?> findClass(String name) {
        return Expect.compute(delegateFindClass, name).getValue();
    }

    @Nullable
    public Class<?> findClass(String module, String name) {
        return Expect.compute(delegateFindModuleClass, module, name).getValue();
    }

}
