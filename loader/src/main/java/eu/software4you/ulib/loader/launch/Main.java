package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.dependencies.Dependencies;
import eu.software4you.ulib.core.dependencies.Repository;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;

final public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Installer.installTo(Main.class.getClassLoader());
        Dependencies.requireInject("{{maven.jopt-simple}}", Repository.mavenCentral(), Main.class.getClassLoader());
        Launcher.launch(args);
    }

}
