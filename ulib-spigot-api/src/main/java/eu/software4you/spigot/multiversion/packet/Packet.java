package eu.software4you.spigot.multiversion.packet;

import eu.software4you.spigot.multiversion.MultiversionManager;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Packet {

    private final Class<?> packet;
    private Constructor<?> constructor;
    private Object[] parameters;

    public Packet(String packetname) {
        this.packet = MultiversionManager.netMinecraftServer(packetname);
    }

    public Packet setConstructor(Class<?>... parameterTypes) {
        try {
            constructor = packet.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Packet setParameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    public Object invoke() {
        Validate.notNull(constructor);
        Validate.notNull(parameters);
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
