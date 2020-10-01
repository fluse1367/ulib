package eu.software4you.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathHacker {

    private static final Class<?>[] parameters = new Class[]{URL.class};

    /**
     * Loads a JAR-File (from {@link File}) into the Runtime
     *
     * @param f file
     * @throws IOException if file could not be added to system classloader
     */
    public static void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }


    /**
     * Loads a JAR-File (from {@link URL}) into the Runtime, can be a remote File
     *
     * @param u url
     * @throws IOException if url could not be added to system classloader
     */
    public static void addURL(URL u) throws IOException {

        ClassLoader cl = ClassPathHacker.class.getClassLoader();

        URLClassLoader ucl;
        if (cl instanceof URLClassLoader)
            ucl = (URLClassLoader) cl;
        else
            ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();

        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(ucl, u);
        } catch (Throwable t) {
            throw new IOException("Could not add URL to system classloader", t);
        }

    }

}