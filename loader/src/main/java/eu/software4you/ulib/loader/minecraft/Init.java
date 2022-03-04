package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.loader.install.Installer;

final class Init {
    static void init() {
        Installer.installMe();
        var cl = ReflectUtil.getCallerClass();
        var module = cl.getModule();
        var other = Installer.getModule();
        if (module.isNamed() && !module.canRead(other))
            module.addReads(other);
    }
}
