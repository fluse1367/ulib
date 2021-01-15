package eu.software4you.ulib;

public class Bootstrap {

    public static void main(String[] args) {
        ULib.makeReady();
        new Launcher(ULib.getInstance()).launch(args);
    }

}
