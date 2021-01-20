package eu.software4you.ulib;

import com.google.gson.internal.JavaVersion;
import eu.software4you.aether.Dependencies;
import eu.software4you.aether.Repository;
import eu.software4you.utils.UtilsInit;

public class Bootstrap {

    public static void main(String[] args) {
        if (JavaVersion.isJava9OrLater()) {
            UtilsInit.jarLoader(Agent::add);
        }

        ULib.makeReady();

        Dependencies.depend("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", Repository.SONATYPE);
        Launcher.instance = ULib.getInstance();
        Launcher.launch(args);
    }

}
