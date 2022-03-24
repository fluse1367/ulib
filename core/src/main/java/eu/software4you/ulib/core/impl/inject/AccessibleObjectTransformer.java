package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.util.Expect;
import javassist.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.AccessibleObject;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessibleObjectTransformer implements ClassFileTransformer {
    public static final String SUDO_KEY = "ulib.sudo";

    public static void acquirePrivileges() {
        Set<Module> permitted = AccessibleObjectTransformer.class.getModule().getLayer().modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .collect(Collectors.toSet());
        System.getProperties().put(SUDO_KEY, (Predicate<Module>) permitted::contains);

        var inst = Internal.getInstrumentation();
        inst.addTransformer(new AccessibleObjectTransformer(), true);
        Expect.compute(() -> inst.retransformClasses(AccessibleObject.class)).getCaught().ifPresent(cause -> {
            throw new RuntimeException("Failed to transform object", cause);
        });
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        final String className = clName.replace('/', '.');
        if (!className.equals("java.lang.reflect.AccessibleObject"))
            return byteCode;


        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            pool.importPackage("java.util.function");

            CtClass cc = pool.get(className);

            var cm = cc.getMethod("checkCanSetAccessible", "(Ljava/lang/Class;Ljava/lang/Class;)V");

            cm.insertBefore("""
                    if (((Predicate) System.getProperties().get((Object) "%s")).test($1.getModule())) {
                        return;
                    }
                    """.formatted(SUDO_KEY));

            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }

}
