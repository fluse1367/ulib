package eu.software4you.ulib;

import eu.software4you.aether.Repository;
import eu.software4you.common.collection.Pair;

public class Bootstrap {

    public static void main(String[] args) {
        Properties.getInstance().ADDITIONAL_LIBS.put("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", new Pair<>(
                "joptsimple.OptionParser", Repository.SONATYPE
        ));
        ULib.init();

        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
