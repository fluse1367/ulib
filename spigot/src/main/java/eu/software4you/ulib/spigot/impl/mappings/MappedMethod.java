package eu.software4you.ulib.spigot.impl.mappings;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

final class MappedMethod extends Mapped<Method> implements eu.software4you.ulib.spigot.mappings.MappedMethod {
    private final MappedClass parent;
    private final MappedClass returnType;
    private final MappedClass[] parameterTypes;

    MappedMethod(MappedClass parent, MappedClass returnType, MappedClass[] parameterTypes, String sourceName, String mappedName) {
        super(sourceName, mappedName);
        this.parent = parent;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    @NotNull
    @Override
    public MappedClass returnType() {
        return returnType;
    }

    @NotNull
    @Override
    public MappedClass[] parameterTypes() {
        return parameterTypes;
    }

    @SneakyThrows
    @Override
    public @NotNull Method find() {
        Class<?>[] types = new Class[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = parameterTypes[i].find();
        }
        return parent.find().getDeclaredMethod(mappedName(), types);
    }
}
