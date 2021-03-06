package eu.software4you.ulib.core.inject;

import eu.software4you.ulib.core.function.BiParamFunc;
import eu.software4you.ulib.core.function.ParamFunc;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * A container for class loading hooks. The hooks may throw exceptions,
 * however in this case the respective method will return {@code null}.
 *
 * @see InjectUtil
 */
@SuppressWarnings("JavadocReference")
public final class ClassLoaderDelegation {

    /**
     * Flag for enabling delegation of {@link ClassLoader#loadClass(String)}.
     */
    public static final int FLAG_DELEGATE_LOAD_CLASS = 0x01;
    /**
     * Flag for enabling delegation of {@link ClassLoader#findClass(String)}.
     */
    public static final int FLAG_DELEGATE_FIND_CLASS = 0x02;
    /**
     * Flag for enabling delegation of {@link ClassLoader#findClass(String, String)}.
     */
    public static final int FLAG_DELEGATE_FIND_MODULE_CLASS = 0x04;
    /**
     * Flag for enabling delegation of {@link ClassLoader#findResource(String)}.
     */
    public static final int FLAG_DELEGATE_FIND_RESOURCE = 0x08;
    /**
     * Flag for enabling delegation of {@link ClassLoader#findResource(String, String)}.
     */
    public static final int FLAG_DELEGATE_FIND_MODULE_RESOURCE = 0x10;

    // default delegation does nothing
    private static final BiParamFunc<String, Boolean, Class<?>, Exception> DEFAULT_DELEGATE_LOAD_CLASS = (name, resolve) -> null;
    private static final ParamFunc<String, Class<?>, Exception> DEFAULT_DELEGATE_FIND_CLASS = (name) -> null;
    private static final BiParamFunc<String, String, Class<?>, Exception> DEFAULT_DELEGATE_FIND_MODULE_CLASS = (module, name) -> null;
    private static final ParamFunc<String, URL, IOException> DEFAULT_DELEGATE_FIND_RESOURCE = (name) -> null;
    private static final BiParamFunc<String, String, URL, IOException> DEFAULT_DELEGATE_FIND_MODULE_RESOURCE = (module, name) -> null;

    private final ClassLoader target;
    private final BiParamFunc<String, Boolean, Class<?>, Exception> delegateLoadClass;
    private final ParamFunc<String, Class<?>, Exception> delegateFindClass;
    private final BiParamFunc<String, String, Class<?>, Exception> delegateFindModuleClass;
    private final BiParamFunc<String, String, URL, IOException> delegateFindModuleResource;
    private final ParamFunc<String, URL, IOException> delegateFindResource;
    final AtomicReference<Map<String, Map<Class<?>[], Function<Object[], Optional<String>>>>> additional =
            new AtomicReference<>(new HashMap<>());

    /**
     * Constructs a delegation container that forwards any class loading request to the specified function.
     *
     * @param delegateLoadClass       see {@link ClassLoader#loadClass(String)}
     * @param delegateFindClass       see {@link ClassLoader#findClass(String)}
     * @param delegateFindModuleClass see {@link ClassLoader#findClass(String, String)}
     */
    public ClassLoaderDelegation(@NotNull BiParamFunc<String, Boolean, Class<?>, Exception> delegateLoadClass,
                                 @NotNull ParamFunc<String, Class<?>, Exception> delegateFindClass,
                                 @NotNull BiParamFunc<String, String, Class<?>, Exception> delegateFindModuleClass) {
        this(delegateLoadClass, delegateFindClass, delegateFindModuleClass,
                DEFAULT_DELEGATE_FIND_MODULE_RESOURCE, DEFAULT_DELEGATE_FIND_RESOURCE);
    }

    /**
     * Constructs a delegation container that forwards any class & resource loading request to the specified function.
     *
     * @param delegateLoadClass          see {@link ClassLoader#loadClass(String)}
     * @param delegateFindClass          see {@link ClassLoader#findClass(String)}
     * @param delegateFindModuleClass    see {@link ClassLoader#findClass(String, String)}
     * @param delegateFindModuleResource see {@link ClassLoader#findResource(String, String)}
     * @param delegateFindResource       see {@link ClassLoader#findResource(String)}
     */
    public ClassLoaderDelegation(@NotNull BiParamFunc<String, Boolean, Class<?>, Exception> delegateLoadClass,
                                 @NotNull ParamFunc<String, Class<?>, Exception> delegateFindClass,
                                 @NotNull BiParamFunc<String, String, Class<?>, Exception> delegateFindModuleClass,
                                 @NotNull BiParamFunc<String, String, URL, IOException> delegateFindModuleResource,
                                 @NotNull ParamFunc<String, URL, IOException> delegateFindResource) {
        this.target = null;
        this.delegateLoadClass = delegateLoadClass;
        this.delegateFindClass = delegateFindClass;
        this.delegateFindModuleClass = delegateFindModuleClass;
        this.delegateFindModuleResource = delegateFindModuleResource;
        this.delegateFindResource = delegateFindResource;
    }

    /**
     * Constructs a delegation container that forwards any class loading request to the specified class loader.
     * The delegation <b>won't</b> forward resource loading requests.
     *
     * @param delegateTarget the class loader the requests should be forwarded to
     */
    public ClassLoaderDelegation(@NotNull ClassLoader delegateTarget) {
        this(delegateTarget,
                FLAG_DELEGATE_LOAD_CLASS
                | FLAG_DELEGATE_FIND_CLASS
                | FLAG_DELEGATE_FIND_MODULE_CLASS);
    }

    /**
     * Constructs a delegation container that forwards requests to the specified class loader.
     *
     * @param delegateTarget the class loader the requests should be forwarded to
     * @param flag           bitset consisting of {@link #FLAG_DELEGATE_LOAD_CLASS}, {@link #FLAG_DELEGATE_FIND_CLASS},
     *                       {@link #FLAG_DELEGATE_FIND_MODULE_CLASS}, {@link #FLAG_DELEGATE_FIND_RESOURCE},
     *                       {@link #FLAG_DELEGATE_FIND_MODULE_RESOURCE}
     * @see eu.software4you.ulib.core.util.Bitmask
     */
    public ClassLoaderDelegation(@NotNull ClassLoader delegateTarget, int flag) {
        this.target = Objects.requireNonNull(delegateTarget);

        if ((flag & FLAG_DELEGATE_LOAD_CLASS) != 0) {
            this.delegateLoadClass = (name, resolve) -> ReflectUtil.doPrivileged(() ->
                    ReflectUtil.icall(Class.class, target, "loadClass()",
                            Param.listOf(String.class, name, boolean.class, resolve)).getValue()
            );
        } else {
            this.delegateLoadClass = DEFAULT_DELEGATE_LOAD_CLASS;
        }

        if ((flag & FLAG_DELEGATE_FIND_CLASS) != 0) {
            this.delegateFindClass = name -> ReflectUtil.doPrivileged(() ->
                    ReflectUtil.icall(Class.class, target, "findClass()",
                            Param.listOf(String.class, name)).getValue()
            );
        } else {
            this.delegateFindClass = DEFAULT_DELEGATE_FIND_CLASS;
        }

        if ((flag & FLAG_DELEGATE_FIND_MODULE_CLASS) != 0) {
            this.delegateFindModuleClass = (module, name) -> ReflectUtil.doPrivileged(() ->
                    ReflectUtil.icall(Class.class, target, "findClass()",
                            Param.listOf(String.class, module, String.class, name)).getValue()
            );
        } else {
            this.delegateFindModuleClass = DEFAULT_DELEGATE_FIND_MODULE_CLASS;
        }

        if ((flag & FLAG_DELEGATE_FIND_RESOURCE) != 0) {
            this.delegateFindResource = name -> ReflectUtil.doPrivileged(() ->
                    ReflectUtil.icall(URL.class, target, "findResource()",
                            Param.listOf(String.class, name)).getValue()
            );
        } else {
            this.delegateFindResource = DEFAULT_DELEGATE_FIND_RESOURCE;
        }

        if ((flag & FLAG_DELEGATE_FIND_MODULE_RESOURCE) != 0) {
            this.delegateFindModuleResource = (module, name) -> ReflectUtil.doPrivileged(() ->
                    ReflectUtil.icall(URL.class, target, "findResource()",
                            Param.listOf(String.class, module, String.class, name)).getValue()
            );
        } else {
            this.delegateFindModuleResource = DEFAULT_DELEGATE_FIND_MODULE_RESOURCE;
        }

    }

    /**
     * Marks an additional method to be injected as well.
     *
     * @param method            the name of the method
     * @param paramTypes        the method's parameter types
     * @param classNameResolver a function which is able to resolve the class name that should be loaded out of the calling parameters
     */
    public void additionally(@NotNull String method, @NotNull Class<?>[] paramTypes, @NotNull Function<? super Object[], Optional<String>> classNameResolver) {
        if (this.additional.getPlain() == null)
            throw new IllegalStateException();

        this.additional.getPlain()
                .computeIfAbsent(method, __ -> new HashMap<>())
                .computeIfAbsent(paramTypes, __ -> classNameResolver::apply);

    }

    @Nullable
    public Class<?> loadClass(@NotNull String name, boolean resolve) {
        return Expect.compute(delegateLoadClass, name, resolve).orElse(null);
    }

    @Nullable
    public Class<?> findClass(@NotNull String name) {
        return Expect.compute(delegateFindClass, name).orElse(null);
    }

    @Nullable
    public Class<?> findClass(@NotNull String module, @NotNull String name) {
        return Expect.compute(delegateFindModuleClass, module, name).orElse(null);
    }

    @Nullable
    public URL findResource(@NotNull String module, @NotNull String name) {
        return Expect.compute(delegateFindModuleResource, module, name).orElse(null);
    }

    @Nullable
    public URL findResource(@NotNull String name) {
        return Expect.compute(delegateFindResource, name).orElse(null);
    }

    @Override
    @NotNull
    public String toString() {
        return target != null ?
                "ClassLoaderDelegation[target=%s,resource-delegation:%s]".formatted(target,
                        delegateFindResource != DEFAULT_DELEGATE_FIND_RESOURCE || delegateFindModuleResource != DEFAULT_DELEGATE_FIND_MODULE_RESOURCE)
                : "ClassLoaderDelegation{" +
                  "delegateLoadClass=" + delegateLoadClass +
                  ", delegateFindClass=" + delegateFindClass +
                  ", delegateFindModuleClass=" + delegateFindModuleClass +
                  ", delegateFindModuleResource=" + delegateFindModuleResource +
                  ", delegateFindResource=" + delegateFindResource +
                  '}';
    }
}
