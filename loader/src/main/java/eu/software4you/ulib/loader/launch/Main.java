package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.core.api.dependencies.Repositories;
import lombok.SneakyThrows;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Class.forName("eu.software4you.ulib.loader.Init", true, Main.class.getClassLoader());
        Dependencies.depend("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", Repositories.of("sonatype"));
        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
