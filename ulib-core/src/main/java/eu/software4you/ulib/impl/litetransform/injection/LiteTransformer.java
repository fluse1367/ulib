package eu.software4you.ulib.impl.litetransform.injection;

import eu.software4you.litetransform.injection.InjectionPoint;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import static eu.software4you.ulib.impl.litetransform.injection.TransformUtil.minfo;

public final class LiteTransformer implements ClassFileTransformer {
    private final Method source;
    private final byte[] sourceByteCode;

    private final String className;
    private final InjectionPoint at;
    private final int ordinal;
    private final String methodSignature;
    private final String methodName;

    @SneakyThrows
    public LiteTransformer(Method source, String className, InjectionPoint at, int ordinal, String methodName, String methodSignature) {
        this.source = source;

        ClassFile cf = new ClassFile(new DataInputStream(source.getDeclaringClass().getProtectionDomain().getCodeSource().getLocation().openStream()));
        MethodInfo mifo = minfo(cf, source);
        this.sourceByteCode = mifo.getCodeAttribute().getCode();


        this.className = className;
        this.at = at;
        this.ordinal = ordinal;
        this.methodName = methodName;
        this.methodSignature = methodSignature;

    }

    @SneakyThrows
    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        if (!clName.replace('/', '.').equals(this.className))
            return byteCode;


        // low level byte code manipulation
        {
            ClassFile cf = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
            MethodInfo mifo = minfo(cf, methodName, methodSignature);

            // TODO: transform method body
            /* Idea:
                ... // <- other code

                // INJECTION START
                Callback cb = new Callback(...);
                ... // <- injected code
                if (cb.hasReturnValue()) {
                    return cb.getReturnValue();
                } else if (cb.isCanceled()) {
                    return;
                }
                // INJECTION END

                ... // <- other code
             */
            Bytecode injectionCallbackCreation = new Bytecode(mifo.getConstPool(), 1, 0);
            // TODO ...
            Bytecode injectionCallbackEnd = new Bytecode(mifo.getConstPool(), 1, 0);
            // TODO ...

            CodeIterator ci = mifo.getCodeAttribute().iterator();
            // TODO: move to correct location
            ci.insert(injectionCallbackCreation.get());
            ci.insert(this.sourceByteCode); // TODO: replace return statements with callback calls?
            ci.insert(injectionCallbackEnd.get());

            // TODO: does CodeIterator reflect changes back to MethodInfo/ClassFile?

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            cf.write(new DataOutputStream(bout));
            return bout.toByteArray();
        }

        // other method
        /*{
            ClassPool cPool = new ClassPool(true); // ClassPool.getDefault()?
            cPool.appendClassPath(new LoaderClassPath(loader));
            cPool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            CtClass ctClazz = cPool.get(className);
            CtMethod method = ctClazz.getMethod(methodName, methodSignature);

            ctClazz.removeMethod(method);

            // TODO: CodeConverter?
            CodeConverter converter = new CodeConverter();

            method.instrument();
            // ---

            String newCode = "System.out.println(\"\\n\\t-->Invoked method [" + className + "." + method.getName() + "(" + method.getSignature() + ")]\");";
            method.insertBefore(newCode);

            ctClazz.addMethod(method);

            return ctClazz.toBytecode();
        }*/
    }
}
