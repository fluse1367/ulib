package eu.software4you.ulib.core.impl.reflect;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.reflect.*;

import java.util.*;
import java.util.regex.Pattern;

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
            method.setAccessible(true);

            var result = method.invoke(invoke, toParameterObjects(frame.getParams()));
            return new Pair<>(method.getReturnType(), result);
        }

        var field = ReflectUtil.findUnderlyingField(clazz, name, true)
                .orElseThrow(() -> new NoSuchFieldException("%s in %s".formatted(name, clazz)));
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
}
