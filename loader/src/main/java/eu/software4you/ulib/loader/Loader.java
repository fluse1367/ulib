package eu.software4you.ulib.loader;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.loader.agent.AgentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Loader {
    public Loader() {

    }

    @SneakyThrows
    public void load() {
        var files = new Extractor().extract();
        var urls = Arrays.stream(files).map(this::toUrl).toArray(URL[]::new);

        URLClassLoader classLoader = new URLClassLoader(urls);

        if (!System.getProperties().containsKey("ulib.javaagent")) {
            new AgentInstaller().install();
        }

        ULib.init();
    }

    @SneakyThrows
    private URL toUrl(File f) {
        return f.toURI().toURL();
    }
}
