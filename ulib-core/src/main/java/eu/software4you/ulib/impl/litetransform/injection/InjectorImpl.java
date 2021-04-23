package eu.software4you.ulib.impl.litetransform.injection;

import eu.software4you.litetransform.LiteTransform;
import eu.software4you.litetransform.injection.Inject;
import eu.software4you.litetransform.injection.InjectionPoint;
import eu.software4you.litetransform.injection.Injector;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

import static eu.software4you.ulib.impl.litetransform.injection.TransformUtil.getSignature;

@Impl(LiteTransform.class)
final class InjectorImpl implements Injector {
    @Await
    private static Agent agent;

    private void validate() {
        if (!Agent.available())
            throw new IllegalStateException("LiteTransform Injection not available!");
    }

    @Override
    public void injectFrom(Class<?> clazz) {
        validate();

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Inject.class))
                continue;
            Inject inject = method.getAnnotation(Inject.class);
            inject(method, inject.method(), inject.signature(),
                    inject.clazz(), inject.at(), inject.ordinal());
        }
    }

    @Override
    public void inject(Method source, Method into, InjectionPoint at, int ordinal) {
        validate();
        inject(source, into.getName(), getSignature(into),
                into.getDeclaringClass().getName(), at, ordinal);
    }

    @SneakyThrows
    @Override
    public void inject(Method source, String methodName, String methodSignature, String className, InjectionPoint at, int ordinal) {
        validate();

        agent.transform(Class.forName(className), new LiteTransformer(source,
                className, at, ordinal, methodName, methodSignature));
        // ---

        // idk what this even is supposed to be
        /*{
            ClassFile cf = new ClassFile(new DataInputStream(source.getDeclaringClass().getProtectionDomain().getCodeSource().getLocation().openStream()));

            String sourceName = source.getName();
            String sourceSig = getSignature(source);
            MethodInfo mifo = cf.getMethods().stream()
                    .filter(info -> info.getDescriptor().equals(sourceSig) && info.getName().equals(sourceName))
                    .findFirst().orElseThrow(IllegalStateException::new);

            Bytecode b = new Bytecode(mifo.getConstPool());
            mifo.set

            CodeAttribute ca = mifo.getCodeAttribute();
            CodeIterator ci = ca.iterator();
            while (ci.hasNext()) {
                ci.append()
            }

            // figure out source code
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(new LoaderClassPath(src.getClassLoader()));
            pool.appendClassPath(new LoaderClassPath(tar.getClassLoader()));

            CtClass cc = pool.get(tar.getSimpleName());
            cc.getMethod(into.getName(), getSignature(into));
        }*/
    }
}
