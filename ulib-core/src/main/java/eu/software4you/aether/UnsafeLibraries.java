package eu.software4you.aether;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.ClassPathHacker;
import eu.software4you.utils.ClassUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

public class UnsafeLibraries {
    private final Logger logger;
    private final String agent;

    @SneakyThrows
    public UnsafeLibraries(Logger logger, String agent) {
        this.logger = logger;
        this.agent = agent;
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

    public void require(String coords, String testClass) {
        logger.fine(String.format("Soft-Requiring %s from maven central repo without further dependency resolving", coords));
        classTest(testClass, coords, () -> require(coords));
    }

    @SneakyThrows
    public void require(String coords) {
        logger.fine(String.format("Requiring %s from maven central repo without further dependency resolving", coords));

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

            URL requestURL = new URL("https://repo1.maven.org/maven2/" + request);

            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestProperty("User-Agent", agent);
            ReadableByteChannel in = Channels.newChannel(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(dest);
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
            out.close();
            in.close();
        }

        ClassPathHacker.addFile(dest);
    }

}
