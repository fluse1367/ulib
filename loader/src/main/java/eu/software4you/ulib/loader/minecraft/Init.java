package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.Installer;

final class Init {
    static void init(Class<?> cl) {
        Installer.installTo(cl.getClassLoader());
        var module = cl.getModule();
        if (module.isNamed())
            module.addReads(Installer.getModule());
    }
}
