package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import lombok.SneakyThrows;

final public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Class.forName("eu.software4you.ulib.loader.install.Installer", true, Main.class.getClassLoader());
        Dependencies.depend("{{maven.jopt-simple}}");
        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
