package eu.software4you.ulib;

import eu.software4you.common.collection.Pair;

public class Bootstrap {

    public static void main(String[] args) {
        Properties.getInstance().ADDITIONAL_LIBS.add(new Pair<>("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", "sonatype"));
        ULib.init();

        Launcher.logger = ULib.logger();
        Launcher.launch(args);
    }

}
