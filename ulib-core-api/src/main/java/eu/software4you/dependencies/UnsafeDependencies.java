package eu.software4you.dependencies;

import eu.software4you.http.HttpUtil;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.ULib;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.IOUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Logger;

/**
 * Maven artifact (down)loading without further resolving.
 */
@RequiredArgsConstructor
public class UnsafeDependencies {
    private final Logger logger;
    private final HttpClient httpClient;
    private final String url;

    public UnsafeDependencies(Logger logger, String agent) {
        this(logger, HttpUtil.buildBasicClient(agent), "https://repo1.maven.org/maven2/");
    }

    public UnsafeDependencies(Logger logger, String agent, String url) {
        this(logger, HttpUtil.buildBasicClient(agent), url);
    }

    @SneakyThrows
    static void classTest(String testClass, ClassLoader cl, String coords, Runnable toRun) {
        if (ClassUtils.isClass(testClass)) {
            // if this point is reached, the test class is already loaded, which means there is no need to download the library
            Class<?> testClazz = Class.forName(testClass, true, cl);
            File file = new File(testClazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            ULib.logger().fine(() -> String.format("Class %s of library %s is already loaded in the runtime: %s",
                    testClass, coords, file.getName()));
            return;
        }
        //  we need to download the library and attach it to classpath
        try {
            toRun.run();
            // library successfully required and attached to classpath
        } catch (Exception e) {
            throw new Exception(String.format("An error occurred while loading library %s", coords), e);
        }
        try {
            // check if testClass is accessible (should be at this point)
            Class<?> testClazz = Class.forName(testClass, true, cl);
            // if this point is reached, the test class was successfully downloaded and added to the classpath
            File file = new File(testClazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            ULib.logger().fine(() -> String.format("Class %s of library %s successfully loaded into the runtime: %s",
                    testClass, coords, file.getName()));
        } catch (Throwable thr) {
            // Class.forName(String) failed (again), library was not loaded (should never happen)
            throw new Exception(String.format("Class %s of library %s was not loaded", testClass, coords), thr);
        }
    }


    public void depend(String coords, String testClass) {
        depend(coords, testClass, ReflectUtil.getCallerClass().getClassLoader());
    }

    public void depend(String coords, String testClass, boolean fallback) {
        depend(coords, testClass, ReflectUtil.getCallerClass().getClassLoader(), fallback);
    }

    public void depend(String coords, String testClass, ClassLoader cl) {
        depend(coords, testClass, cl, false);
    }

    public void depend(String coords, String testClass, ClassLoader cl, boolean fallback) {
        logger.fine(String.format("Depending on %s from '%s' repo without further dependency resolving", coords, url));
        classTest(testClass, cl, coords, () -> depend(coords, cl, fallback));
    }


    public void depend(String coords) {
        depend(coords, ReflectUtil.getCallerClass().getClassLoader());
    }

    public void depend(String coords, boolean fallback) {
        depend(coords, ReflectUtil.getCallerClass().getClassLoader(), fallback);
    }

    public void depend(String coords, ClassLoader cl) {
        depend(coords, cl, false);
    }

    @SneakyThrows
    public void depend(String coords, ClassLoader cl, boolean fallback) {
        logger.fine(String.format("Depending on %s from '%s' repo without further dependency resolving", coords, url));

        File root = ULib.get().getLibrariesUnsafeDir();

        String[] parts = coords.split(":");

        String group = parts[0];
        String name = parts[1];
        String version = parts[2];

        String request = String.format("%s/%s/%s/%s-%s.jar",
                group.replace(".", "/"), name, version, name, version);

        File dest = new File(root, request);

        if (!dest.exists()) {
            dest.getParentFile().mkdirs();

            try (val in = HttpUtil.getContent(httpClient, url + request);
                 val out = new FileOutputStream(dest)) {
                IOUtil.write(in, out);
            }
        }

        DependencyLoader.load(dest, cl, fallback);
    }

}
