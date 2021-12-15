package eu.software4you.ulib.loader.agent;

import lombok.SneakyThrows;

import java.io.File;
import java.util.jar.JarFile;

final class AgentUtil {
    @SneakyThrows
    static String getVer(File jar) {
        return new JarFile(jar).getManifest().getMainAttributes().getValue("Implementation-Version");
    }

    static String getJavaBin() {
        return ProcessHandle.current().info().command().orElseGet(() -> {
            String bin = String.format("%s%sbin%2$sjava", System.getProperty("java.home"), File.separator);
            return !new File(bin).exists() && !new File(bin += ".exe").exists() ? null : bin;
        });
    }
}
