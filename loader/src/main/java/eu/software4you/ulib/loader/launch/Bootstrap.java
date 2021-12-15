package eu.software4you.ulib.loader.launch;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.impl.Properties;

public class Bootstrap {

    public static void main(String[] args) {
        Properties.getInstance().ADDITIONAL_LIBS.add(new Pair<>("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", "sonatype"));
        ULib.init();

        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
