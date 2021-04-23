package eu.software4you.ulib.impl.litetransform.injection;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

class TransformUtil {
    static MethodInfo minfo(ClassFile cf, Method method) {
        return minfo(cf, method.getName(), getSignature(method));
    }

    static MethodInfo minfo(ClassFile cf, String methodName, String methodSignature) {
        return cf.getMethods().stream()
                .filter(info -> info.getName().equals(methodName) && info.getDescriptor().equals(methodSignature))
                .findFirst().orElseThrow(IllegalStateException::new);
    }

    @SneakyThrows
    static String getSignature(Method method) { // from https://stackoverflow.com/a/45122250/8400001
        Field signatureField = Method.class.getDeclaredField("signature");
        signatureField.setAccessible(true);
        String signature = (String) signatureField.get(method);
        if (signature != null) {
            return signature;
        }

        StringBuilder b = new StringBuilder("(");
        for (Class<?> c : method.getParameterTypes()) {
            signature = Array.newInstance(c, 0).toString();
            b.append(signature, 1, signature.indexOf('@'));
        }
        b.append(')');
        if (method.getReturnType() == void.class) {
            b.append("V");
        } else {
            signature = Array.newInstance(method.getReturnType(), 0).toString();
            b.append(signature, 1, signature.indexOf('@'));
        }
        return b.toString();
    }
}
