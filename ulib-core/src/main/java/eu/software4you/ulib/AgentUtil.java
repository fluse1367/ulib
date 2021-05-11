package eu.software4you.ulib;

import lombok.SneakyThrows;

import java.io.File;
import java.util.jar.JarFile;

final class AgentUtil {
    @SneakyThrows
    static String getVer(File jar) {
        return new JarFile(jar).getManifest().getMainAttributes().getValue("Implementation-Version");
    }
}
