package eu.software4you.aether;

import eu.software4you.http.HttpUtil;
import eu.software4you.ulib.ULib;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.JarLoader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UnsafeDependencies {
    private final Logger logger;
    private final HttpClient client;
    private final String url;

    public UnsafeDependencies(Logger logger, String agent) {
        this(logger, HttpUtil.buildBasicClient(agent), "https://repo1.maven.org/maven2/");
    }

    @SneakyThrows
    static void classTest(String testClass, String coords, Runnable toRun) {
        if (ClassUtils.isClass(testClass)) {
            // if this point is reached, the test class is already loaded, which means there is no need to download the library
            Class<?> testClazz = Class.forName(testClass);
            File file = new File(testClazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            ULib.getInstance().getLogger().fine(String.format("Class %s of library %s is already loaded in the runtime: %s",
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
            Class<?> testClazz = Class.forName(testClass);
            // if this point is reached, the test class was successfully downloaded and added to the classpath
            File file = new File(testClazz.getProtectionDomain().getCodeSource().getLocation().toURI());
            ULib.getInstance().getLogger().fine(String.format("Class %s of library %s successfully loaded into the runtime: %s",
                    testClass, coords, file.getName()));
        } catch (Throwable thr) {
            // Class.forName(String) failed (again), library was not loaded (should never happen)
            throw new Exception(String.format("Class %s of library %s was not loaded", testClass, coords), thr);
        }
    }

    public void depend(String coords, String testClass) {
        logger.fine(String.format("Depending on %s from '%s' repo without further dependency resolving", coords, url));
        classTest(testClass, coords, () -> depend(coords));
    }

    @SneakyThrows
    public void depend(String coords) {
        logger.fine(String.format("Depending on %s from '%s' repo without further dependency resolving", coords, url));

        File root = ULib.getInstance().getLibsUnsafeDir();

        String[] parts = coords.split(":");

        String group = parts[0];
        String name = parts[1];
        String version = parts[2];

        String request = String.format("%s/%s/%s/%s-%s.jar",
                group.replace(".", "/"), name, version, name, version);

        File dest = new File(root, request);

        if (!dest.exists()) {
            dest.getParentFile().mkdirs();

            ReadableByteChannel in = Channels.newChannel(HttpUtil.getContent(client, url + request));
            FileOutputStream out = new FileOutputStream(dest);
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
            IOUtils.close(out, in);
        }

        JarLoader.load(dest);
    }

}
