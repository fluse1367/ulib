package eu.software4you.reflect;

import eu.software4you.utils.ClassUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Deprecated
public class UniClass {
    private final Class<?> clazz;

    public UniClass(Class<?> clazz) {
        this.clazz = clazz;
        if (clazz == null)
            throw new RuntimeException("Class is null.");
    }

    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * @param parameterTypes the class types of the given arguments in the constructor.
     * @return instance of UniClassConstructor
     */
    public UniClassConstructor getConstructor(Class<?>... parameterTypes) {
        return new UniClassConstructor(clazz, parameterTypes);
    }

    /**
     * @param name           the name of the Method.
     * @param parameterTypes the class types of the given arguments.
     * @return instance of UniMethod if the Method exists or null if it does not.
     */
    public UniMethod getMethod(String name, Class<?>... parameterTypes) {
        Method method = ClassUtils.findUnderlyingDeclaredMethod(clazz, name, parameterTypes);
        Validate.notNull(method);
        return new UniMethod(method);
    }

    /**
     * @param name the field name.
     * @return the {@code Field} object of this class specified by {@code name}
     */
    public UniField getField(String name) {
        Field field = ClassUtils.findUnderlyingDeclaredField(clazz, name);
        Validate.notNull(field);
        return new UniField(field);
    }

}
