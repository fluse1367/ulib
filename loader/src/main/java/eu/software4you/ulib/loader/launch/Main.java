package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.dependencies.Dependencies;
import eu.software4you.ulib.core.dependencies.Repository;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;

final public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Installer.installMe();
        InjTest.test();
        if (true) return;
        Dependencies.requireInject("{{maven.jopt-simple}}", Repository.mavenCentral(), Main.class.getClassLoader())
                .rethrow();
        Launcher.launch(args);
    }

}
