package eu.software4you.utils;

import java.util.function.Consumer;
import java.util.jar.JarFile;

public class UtilsInit {
    public static void jarLoader(Consumer<JarFile> init) {
        JarLoader.loader = init;
    }
}
