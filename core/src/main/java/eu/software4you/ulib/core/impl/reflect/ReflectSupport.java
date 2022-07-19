package eu.software4you.ulib.core.impl.reflect;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.reflect.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ReflectSupport {
    public static Class<?>[] toParameterTypes(List<Param<?>> params) {
        return params.stream()
                .map(Param::getClazz)
                .toArray(Class[]::new);
    }

    public static Object[] toParameterObjects(List<Param<?>> params) {
        return params.stream()
                .map(Param::getValue)
                .toArray();
    }

    public static Pair<Class<?>, Object> frameCall(CallFrame frame, Class<?> clazz, Object invoke) throws ReflectiveOperationException {
        var name = frame.getName();
        var params = frame.getParams();

        return frame.isField() ? frameCallField(clazz, invoke, name, params) : frameCallMethod(clazz, invoke, name, params);
    }

    private static Pair<Class<?>, Object> frameCallMethod(Class<?> anchor, Object invoke, String identifier, List<Param<?>> params) throws ReflectiveOperationException {
        // gather information
        var types = toParameterTypes(params);
        var method = ReflectUtil.findUnderlyingMethod(anchor, identifier, true, types)
                .orElseThrow(() -> new NoSuchMethodException("%s(%s) in %s".formatted(identifier, Arrays.toString(types), anchor)));

        // make method accessible
        if (!method.canAccess(invoke))
            method.setAccessible(true);

        // execute
        var result = method.invoke(invoke, toParameterObjects(params));
        return new Pair<>(method.getReturnType(), result);
    }


    private static Pair<Class<?>, Object> frameCallField(Class<?> anchor, Object invoke, String identifier, List<Param<?>> params) throws ReflectiveOperationException {
        var field = ReflectUtil.findUnderlyingField(anchor, identifier, true)
                .orElseThrow(() -> new NoSuchFieldException("%s in %s".formatted(identifier, anchor)));

        // make field accessible
        if (!field.canAccess(invoke))
            field.setAccessible(true);

        // put value?
        if (!params.isEmpty()) {
            var param = params.get(0);
            if (param.getClazz() == field.getType()) {
                // update the value
                field.set(invoke, params.get(0).getValue());
            }
        }

        // obtain value
        return new Pair<>(field.getType(), field.get(invoke));
    }

    public static CallFrame[] buildFramePath(String call, List<Param<?>>[] params) {
        var frames = call.split(Pattern.quote("."));
        CallFrame[] path = new CallFrame[frames.length];

        for (int i = 0; i < frames.length; i++) {
            String frame = frames[i];
            List<Param<?>> frameParams = Optional.ofNullable(params.length > i ? params[i] : null).orElseGet(Collections::emptyList);

            CallFrame callFrame;
            if (frame.endsWith("()")) {
                callFrame = new CallFrame(frame.substring(0, frame.length() - 2), false, frameParams);
            } else {
                callFrame = new CallFrame(frame, true, frameParams);
            }

            path[i] = callFrame;
        }

        return path;
    }

    @Deprecated(forRemoval = true)
    public static boolean identifyRecursion(int threshold, int maxPatternLength, int ignoreLeadingFrames) {
        List<Class<?>> pattern = new LinkedList<>(); // pattern will be iterated/modified quite often
        List<Class<?>> stack = ReflectUtil.walkStack(st -> st
                .skip(1) // skip this
                .skip(ignoreLeadingFrames) // skip leading frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .collect(Collectors.toList()));

        int occurrence = 0;

        for (int i = 0; i < stack.size(); i++) {
            // if we can't find the current pattern in the stack, add the frame to the pattern

            // create sublist and compare to pattern
            List<Class<?>> sub;
            try {
                sub = stack.subList(i, i + pattern.size());
            } catch (IndexOutOfBoundsException e) {
                // stack is not big enough to check for any more pattern occurrences
                return false;
            }
            // compare pattern
            if (pattern.equals(sub)) {
                // pattern found, test threshold
                if (++occurrence >= threshold) {
                    // threshold met, indicate "success"
                    return true;
                }

                // jump over to end of pattern
                i += sub.size();
                continue;
            }
            // check chain length
            if (pattern.size() > maxPatternLength)
                return false; // max pattern length reached and no recursion found, abort

            // pattern not found, reset counter
            occurrence = 0;

            // add frame to pattern
            pattern.add(stack.get(i));
        }

        // loop ended and no recursion found
        return false;
    }

    @SneakyThrows
    public static boolean deepEquals(@NotNull Object a, @NotNull Object b) {
        if (!Internal.isSudoThread())
            return ReflectUtil.doPrivileged(() -> deepEquals(a, b));

        // compare all fields
        var clazz = a.getClass();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                final int mod = field.getModifiers();
                if (field.isSynthetic()
                    || Modifier.isTransient(mod)
                    || Modifier.isStatic(mod))
                    continue; // skip on synthetic, transient or static fields

                field.setAccessible(true);

                Object someFieldObj = field.get(a);
                Object otherFieldObj = field.get(b);

                if (!Objects.equals(someFieldObj, otherFieldObj))
                    return false; // fields are not equal
            }

        } while ((clazz = clazz.getSuperclass()) != null && !clazz.isPrimitive() && !clazz.isInterface()
                 && clazz != Object.class);

        // no object found that is not equal
        return true;
    }

    public static int deepHash(@NotNull final Object obj, @NotNull final Class<?> anchor, boolean useImpl) {
        // attempt using implementation
        if (useImpl) {
            try {
                return (int) anchor.getMethod("hashCode").invoke(obj);
            } catch (InvocationTargetException e) {
                var cause = e.getTargetException();
                if (cause instanceof RuntimeException re)
                    throw re; // rethrow

                // definition in Object#hashCode() prevents declaration of check exceptions
                throw new InternalError("Unexpected checked exception", e);
            } catch (IllegalAccessException | ClassCastException e) {
                // should not happen as #hashCode() is defined in Object
                // so, make the compiler happy:
                throw new InternalError(e);
            } catch (NoSuchMethodException e) {
                // fallthrough
            }
        }

        // auto compute hash
        if (!Internal.isSudoThread())
            return ReflectUtil.doPrivileged(() -> deepHash(obj, anchor, false));

        var fields = Arrays.stream(anchor.getDeclaredFields())
                .filter(f -> !f.isSynthetic())
                .filter(f -> {
                    int mod = f.getModifiers();
                    return !Modifier.isStatic(mod) && !Modifier.isTransient(mod);
                })
                .toArray(Field[]::new);
        List<Object> objs = new ArrayList<>(fields.length + 1);

        // collect subsequent object for hash computation
        for (Field declaredField : fields) {
            declaredField.setAccessible(true);
            try {
                objs.add(declaredField.get(obj));
            } catch (IllegalAccessException e) {
                throw new InternalError(e); // should not happen because ulib has sudo privileges
            }
        }

        // superclass hash?
        Class<?> superClazz = anchor.getSuperclass();
        if (superClazz != null && !superClazz.isPrimitive() && !superClazz.isInterface()
            && superClazz != Object.class)
            objs.add(deepHash(obj, superClazz, true));

        return Objects.hash(objs.toArray());
    }
}
