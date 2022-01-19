package eu.software4you.ulib.core.impl.transform;

import eu.software4you.ulib.core.impl.Agent;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.AccessibleObject;
import java.security.ProtectionDomain;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessibleObjectTransformer implements ClassFileTransformer {

    public static void init() {
        var agent = Agent.getInstance();
        agent.addTransformer(new AccessibleObjectTransformer());
        agent.transform(AccessibleObject.class);
    }

    @SneakyThrows
    public static void ulibAllAccessToUnnamed() {
        var method = Module.class.getDeclaredMethod("implAddReadsAllUnnamed");
        method.setAccessible(true);

        var modules = AccessibleObjectTransformer.class.getModule().getLayer().modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .toArray(Module[]::new);

        for (Module module : modules) {
            method.invoke(module);
        }
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

            CtClass cc = pool.get(className);

            var cm = cc.getMethod("checkCanSetAccessible", "(Ljava/lang/Class;Ljava/lang/Class;)V");

            cc.removeMethod(cm);
            cm.insertBefore("""
                    String name = $1.getModule().getName();
                    if (name != null && name.startsWith("ulib.")) {
                        return;
                    }
                    """);
            cc.addMethod(cm);

            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }
}
