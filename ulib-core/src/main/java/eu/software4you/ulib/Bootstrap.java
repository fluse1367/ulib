package eu.software4you.ulib;

public class Bootstrap {

    public static void main(String[] args) {
        Lib instance = ULib.getInstance();
        instance.init();
        new Launcher(instance).launch(args);
    }

}
