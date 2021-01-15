package eu.software4you.ulib;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ULib {

    private static final Init instance;

    static {
        try {
            Class<?> implClazz = Class.forName("eu.software4you.ulib.Impl");
            Constructor<?> implConstructor = implClazz.getDeclaredConstructor();
            implConstructor.setAccessible(true);
            Object impl = implConstructor.newInstance();
            if (impl instanceof Init) {
                instance = (Init) impl;
            } else {
                throw new IllegalStateException("Implementation of wrong type.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No implementation found!", e);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalStateException e) {
            throw new RuntimeException("Invalid implementation.", e);
        }
    }

    public static Lib getInstance() {
        return instance;
    }

    public static void makeReady() {
        instance.init();
    }
}
