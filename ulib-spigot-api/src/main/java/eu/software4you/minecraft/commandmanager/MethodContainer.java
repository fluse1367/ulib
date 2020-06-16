package eu.software4you.minecraft.commandmanager;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public class MethodContainer {
    private final HashMap<BaseCommand.Sender, Method> methods;

    public MethodContainer(final HashMap<BaseCommand.Sender, Method> map) {
        this.methods = map;
    }

    public Method getMethod(final BaseCommand.Sender s) {
        return this.methods.get(s);
    }

    public Collection<Method> getMethods() {
        return this.methods.values();
    }

    public HashMap<BaseCommand.Sender, Method> getMethodMap() {
        return this.methods;
    }
}

