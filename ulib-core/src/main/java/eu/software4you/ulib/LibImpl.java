package eu.software4you.ulib;

import eu.software4you.aether.Dependencies;
import eu.software4you.function.ConstructingFunction;
import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.FileUtils;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

class LibImpl implements Lib {
    static {
        long started = System.currentTimeMillis();
        val impl = new LibImpl();

        // injecting into ULib
        try {
            ReflectUtil.forceCall(ULib.class, null, "impl", Parameter.single(Lib.class, impl));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        impl.info("Loading ...");

        // load/register implementations
        try {
            // loading all classes: eu.software4you.ulib.impl.**Impl
            String pack = String.format("%s/impl/", LibImpl.class.getPackage().getName()).replace(".", "/");
            JarFile jar = new JarFile(FileUtils.getClassFile(LibImpl.class));
            val e = jar.entries();

            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();

                if (name.startsWith(pack) && name.endsWith("Impl.class")) {
                    String clName = name.replace("/", ".").substring(0, name.length() - 6);
                    impl.getLogger().finer(String.format("Loading implementation %s with %s", clName, LibImpl.class.getClassLoader()));
                    val cl = Class.forName(clName);

                    // check for @Impl
                    if (cl.isAnnotationPresent(Impl.class)) {
                        Impl im = cl.getDeclaredAnnotation(Impl.class);

                        // @Impl present, look out of @Await in target type
                        Class<?> type = im.value();
                        for (Field field : type.getDeclaredFields()) {
                            if (!field.isAnnotationPresent(Await.class)
                                    || !Modifier.isStatic(field.getModifiers()))
                                continue;
                            // @Await found, inject
                            field.setAccessible(true);

                            // ConstructingFunction
                            if (field.getType() != type) {
                                if (field.getType() != ConstructingFunction.class) {
                                    continue;
                                }

                                for (Constructor<?> constructor : cl.getDeclaredConstructors()) {
                                    if (!constructor.isAnnotationPresent(ImplConst.class)) {
                                        continue;
                                    }
                                    impl.getLogger().finer(String.format("Injecting %s as constructing function into %s", cl.toString(), field.toString()));
                                    constructor.setAccessible(true);

                                    ConstructingFunction<?> fun = new ConstructingFunction<Object>() {
                                        @SneakyThrows
                                        @Override
                                        public Object apply(Object... objects) {
                                            return constructor.newInstance(objects);
                                        }
                                    };
                                    field.set(null, fun);

                                    break;
                                }

                                break;
                            }
                            impl.getLogger().finer(String.format("Injecting %s into %s", cl.toString(), field.toString()));

                            // direct implementation
                            Constructor<?> constructor = cl.getDeclaredConstructor();
                            constructor.setAccessible(true);

                            field.set(null, constructor.newInstance());

                            break;
                        }
                    }

                    impl.getLogger().finer(String.format("Implementation %s loaded", cl.getName()));
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException("Invalid implementation", e);
        }

        // load dependencies
        try {
            for (val en : impl.properties.ADDITIONAL_LIBS.entrySet()) {
                val v = en.getValue();
                Dependencies.depend(en.getKey(), v.getFirst(), v.getSecond());
            }
        } catch (Exception e) {
            impl.exception(e, "Error while loading dependencies. You might experiencing issues.");
        }

        impl.info(String.format("Done (%ss)!", BigDecimal.valueOf(System.currentTimeMillis() - started)
                .divide(BigDecimal.valueOf(1000), new MathContext(4, RoundingMode.HALF_UP)).toPlainString()
        ));
    }

    private final Logger logger;
    private final Properties properties;
    private final String version;
    private final RunMode runMode;
    private final String name;
    private final String nameOnly;

    private LibImpl() {
        properties = Properties.getInstance();
        version = ULib.class.getPackage().getImplementationVersion();

        runMode = ClassUtils.isClass("eu.software4you.ulib.ULibVelocityPlugin") ? RunMode.VELOCITY
                : ClassUtils.isClass("eu.software4you.ulib.ULibSpigotPlugin") ? RunMode.SPIGOT
                : ClassUtils.isClass("eu.software4you.ulib.ULibBungeecordPlugin") ? RunMode.BUNGEECORD
                : RunMode.STANDALONE;

        nameOnly = "uLib";
        name = String.format("%s-%s", nameOnly, runMode.getName());


        logger = Logger.getLogger(getClass().getName());
        logger.setUseParentHandlers(false);

        if (!properties.NO_SPLASH) {
            System.out.println(properties.BRAND);
            System.out.printf("uLib by software4you.eu, running %s implementation version %s%n", runMode.getName(), version);
            System.out.println("Log level: " + properties.LOG_LEVEL);
            System.out.println("This uLib log file will be placed in: " + properties.DATA_DIR);
        }

        LoggingFactory factory = new LoggingFactory(properties, logger, this);
        factory.prepare();
        factory.systemInstall();


    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public RunMode getMode() {
        return runMode;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameOnly() {
        return nameOnly;
    }

    @Override
    public File getDataDir() {
        return properties.DATA_DIR;
    }

    @Override
    public File getLibsM2Dir() {
        return properties.LIBS_M2_DIR;
    }

    @Override
    public File getLibsUnsafeDir() {
        return properties.LIBS_UNSAFE_DIR;
    }

    @Override
    public void debug(String debug) {
        logger.fine(debug);
    }

    @Override
    public void info(String info) {
        logger.info(info);
    }

    @Override
    public void warn(String warn) {
        logger.warning(warn);
    }

    @Override
    public void error(String error) {
        logger.severe(error);
    }

    @Override
    public void exception(Throwable throwable) {
        exception(throwable, null);
    }

    @Override
    public void exception(Throwable throwable, String msg) {
        if (msg != null && !msg.isEmpty())
            logger.log(Level.SEVERE, msg, throwable);
        else
            logger.log(Level.SEVERE, "An unexpected exception occurred!", throwable);
    }
}
