package eu.software4you.ulib.core.impl.reflect;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.reflect.*;

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

        if (!frame.isField()) {
            var types = toParameterTypes(params);
            var method = ReflectUtil.findUnderlyingMethod(clazz, name, true, types)
                    .orElseThrow(() -> new NoSuchMethodException("%s(%s) in %s".formatted(name, Arrays.toString(types), clazz)));
            if (!method.canAccess(invoke))
                method.setAccessible(true);

            var result = method.invoke(invoke, toParameterObjects(params));
            return new Pair<>(method.getReturnType(), result);
        }

        var field = ReflectUtil.findUnderlyingField(clazz, name, true)
                .orElseThrow(() -> new NoSuchFieldException("%s in %s".formatted(name, clazz)));
        if (!field.canAccess(invoke))
            field.setAccessible(true);

        // put value
        if (!params.isEmpty()) {
            var param = params.get(0);
            if (param.getClazz() == field.getType())
                field.set(invoke, params.get(0).getValue());
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
        List<Class<?>> stack = Arrays.stream(ReflectUtil.getCallerStack()) // stack may be immutable
                .skip(1) // skip this
                .skip(ignoreLeadingFrames) // skip leading frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .collect(Collectors.toList());

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
}
