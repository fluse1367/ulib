package eu.software4you.ulib.test;

import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.loader.install.Installer;
import javassist.*;
import javassist.bytecode.AccessFlag;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import static eu.software4you.ulib.core.util.Conditions.in;
import static org.junit.Assert.assertTrue;

/**
 * Tests that all publicly exposed methods are properly annotated.
 */
public class AnnotationTest {

    @Test
    public void testAnnotations() {

        collectPublicMembers().forEach(ct -> {

            final String errMsg = "Incorrect annotation(s) at %s.%s%s (%s:%%s) : %s"
                    .formatted(ct.getDeclaringClass().getName(),
                            ct instanceof CtConstructor ? "<init>" : ct.getName(), ct.getSignature(),
                            ct.getDeclaringClass().getSimpleName() + ".java",
                            Arrays.toString(Expect.compute(ct::getAnnotations).orElseThrow()));

            if (ct instanceof CtField ctf) {
                // primitive types cannot be null, thus no annotation necessary
                var type = Expect.compute(ctf::getType).orElseThrow();
                if (!annotatable(type))
                    return;

                // fields need to have either @NotNull OR @Nullable
                assertTrue(errMsg.formatted("1"), ctf.hasAnnotation(NotNull.class) != ctf.hasAnnotation(Nullable.class));
                return;
            }

            if (!(ct instanceof CtBehavior ctb)) {
                return;
            }

            // check parameter annotations
            var params = Expect.compute(ctb::getParameterTypes).orElseThrow();
            var annotations = Expect.compute(ctb::getParameterAnnotations).orElseThrow();

            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                if (!annotatable(param))
                    continue; // skip primitives as they cannot be null


                var annos = Stream.of(annotations[i])
                        .map(Annotation.class::cast)
                        .map(Annotation::annotationType)
                        .toArray();

                // params need to have either @NotNull OR @Nullable
                assertTrue((errMsg + " %s (parameter index: %d)").formatted(
                        Math.max(1, ctb.getMethodInfo().getLineNumber(0)), Arrays.toString(annos), i
                ), in(NotNull.class, annos) != in(Nullable.class, annos));
            }


            // check return type annotation
            if (ctb instanceof CtMethod ctm) {
                var ret = Expect.compute(ctm::getReturnType).orElseThrow();

                if (!annotatable(ret))
                    return;

                assertTrue(errMsg.formatted(Math.max(1, ctb.getMethodInfo().getLineNumber(0))) + " (return type)",
                        ctm.hasAnnotation(NotNull.class) != ctm.hasAnnotation(Nullable.class));
            }

        });

    }

    private boolean annotatable(CtClass type) {
        return type != CtClass.voidType && !type.getName().equals("java.lang.Void") && !type.isPrimitive()
               && (!type.isArray() || !resolveArray(type).isPrimitive());
    }

    private CtClass resolveArray(CtClass ct) {
        return ct.isArray() ? resolveArray(Expect.compute(ct::getComponentType).orElseThrow()) : ct;
    }

    private Stream<CtMember> collectPublicMembers() {

        // collect ulib modules
        var modules = Installer.getLayer().modules()
                .stream().filter(m -> m.getName().startsWith("ulib."))
                .toList();


        return modules.stream().flatMap(m -> m.getPackages().stream()
                .filter(pn -> m.isOpen(pn) || m.isExported(pn))
                .flatMap(pack -> {
                    var pool = new ClassPool();
                    pool.appendClassPath(new LoaderClassPath(m.getClassLoader()));
                    pool.appendSystemPath();

                    return findClasses(m.getClassLoader(), pack).stream()
                            .filter(c -> java.lang.reflect.Modifier.isPublic(c.getModifiers()))
                            .map(cl -> Expect.compute(pool::get, cl.getName()).orElseThrow())
                            .flatMap(ct -> Stream.concat(Stream.of(ct.getDeclaredBehaviors()), Stream.of(ct.getDeclaredFields())))
                            .filter(ctm -> Modifier.isPublic(ctm.getModifiers()))
                            .filter(ctm -> (ctm.getModifiers() & AccessFlag.SYNTHETIC) == 0)
                            .filter(ctm -> { // filter out Enum#values()
                                if (!ctm.getDeclaringClass().isEnum()
                                    || !(ctm instanceof CtMethod cm)
                                    || !cm.getName().equals("values"))
                                    return true;
                                return Expect.compute(cm::getParameterTypes)
                                               .map(t -> t.length)
                                               .orElse(0) != 0;
                            })
                            ;
                }));

    }

    @SneakyThrows
    private Set<Class<?>> findClasses(ClassLoader loader, String packageName) {
        var prefix = packageName.replace(".", "/") + "/";
        var res = loader.getResource(prefix);
        if (res == null)
            return Collections.emptySet();

        if (!res.getProtocol().equals("jar"))
            throw new RuntimeException("Cannot find classes from " + res.getProtocol() + " protocol");

        var path = res.getPath();
        var filePath = path.substring(0, path.lastIndexOf(".jar!/") + 4);

        try (var jar = new JarFile(URI.create(filePath).toURL().getFile())) {

            return jar.stream()
                    .map(ZipEntry::getName)
                    .filter(e -> e.startsWith(prefix) && e.endsWith(".class"))
                    .map(e -> e.substring(0, e.length() - 6))
                    .map(e -> e.replace("/", "."))
                    .map(name -> ReflectUtil.forName(name, true, loader).orElseThrow())
                    .collect(Collectors.toSet());

        }

    }

}
