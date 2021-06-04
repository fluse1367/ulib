package eu.software4you.ulib.impl.spigot.mappings;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

final class ObfMethod extends Obf<Method> implements eu.software4you.spigot.mappings.ObfMethod {
    private final ObfClass parent;
    private final ObfClass returnType;
    private final ObfClass[] parameterTypes;

    ObfMethod(ObfClass parent, ObfClass returnType, ObfClass[] parameterTypes, String name, String obfuscatedName) {
        super(name, obfuscatedName);
        this.parent = parent;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    @NotNull
    @Override
    public ObfClass getReturnType() {
        return returnType;
    }

    @NotNull
    @Override
    public ObfClass[] getParameterTypes() {
        return parameterTypes;
    }

    @SneakyThrows
    @Override
    public @NotNull Method find() {
        Class<?>[] types = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = parameterTypes[i].find();
        }
        return parent.find().getDeclaredMethod(getObfuscatedName(), types);
    }
}
