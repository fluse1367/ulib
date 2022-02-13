package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.Installer;

final class Init {
    static void init(Class<?> cl) {
        try {
            Installer.installTo(cl.getClassLoader());
        } catch (IllegalArgumentException e) {
            // loaders were already installed to class loader!
        }
        var module = cl.getModule();
        var other = Installer.getModule();
        if (module.isNamed() && !module.canRead(other))
            module.addReads(other);
    }
}
