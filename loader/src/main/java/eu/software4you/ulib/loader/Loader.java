package eu.software4you.ulib.loader;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Loader {

    static {
        load();
    }

    public static void $() {
        // empty
    }

    @SneakyThrows
    private static void load() {
        var files = new Extractor().extract();
        var urls = Arrays.stream(files)
                .map(Loader::toUrl)
                .toArray(URL[]::new);

        URLClassLoader classLoader = new URLClassLoader(urls);

        if (!System.getProperties().containsKey("ulib.javaagent")) {
            new AgentInstaller().install();
        }

        Class.forName("eu.software4you.ulib.core.ULib", true, classLoader); // ulib init

        // TODO: make ulib available to other classes: inject ulib loading code into other classloaders (transform parent classloader?)
    }

    @SneakyThrows
    private static URL toUrl(File f) {
        return f.toURI().toURL();
    }
}
