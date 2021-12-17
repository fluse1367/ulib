package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;

final public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Installer.installTo(Main.class.getClassLoader());
        Dependencies.depend("{{maven.jopt-simple}}");
        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
