package eu.software4you.ulib;

import eu.software4you.utils.JarLoader;
import joptsimple.*;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

class Launcher {
    static Lib instance;

    @SneakyThrows
    static void launch(String... args) {
        OptionParser parser = new OptionParser();
        parser.accepts("help", "Shows the help message")
                .forHelp();
        OptionSpecBuilder launchOptnBuilder;
        OptionSpec<String> launch = (launchOptnBuilder = parser.accepts("launch", "Launches a jar file"))
                .withRequiredArg().ofType(String.class);
        OptionSpec<String> main = parser.accepts("main", "Starts a main class. Class must be already in classpath")
                .availableUnless("launch")
                .withRequiredArg().ofType(String.class);
        launchOptnBuilder.availableUnless("main");

        OptionSpec<String> mainArgs = parser.accepts("args", "The String[] args that should be passed to the main class")
                .availableIf(launch, main)
                .withRequiredArg()
                .withValuesSeparatedBy(":::")
                .ofType(String.class);

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            instance.error(e.getMessage());
            parser.printHelpOn(System.out);
            return;
        }

        if (!options.hasOptions()) {
            instance.info("This is a library, so it's not designed to run stand-alone.");
            instance.info("https://software4you.eu");
            parser.printHelpOn(System.out);
            System.exit(0);
            return;
        }

        if (options.has("help")) {
            parser.printHelpOn(System.out);
            return;
        }
        if (!(options.has(launch) || options.has(main)))
            return;

        Class<?> mainClass = null;
        if (options.has(launch)) {
            String launchFilePath = options.valueOf(launch);
            File launchFile = new File(launchFilePath);
            if (!launchFile.exists()) {
                instance.warn(String.format("Cannot launch specified file: File %s does not exist.", launchFilePath));
                return;
            }

            JarFile jar = new JarFile(launchFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                instance.warn(String.format("%s does not have a manifest.", launchFile.getName()));
                return;
            }

            Attributes mainAttr = manifest.getMainAttributes();

            String mainClassName = mainAttr.getValue("Main-Class");

            if (mainClassName == null) {
                instance.warn(String.format("%s does not have a main class.", launchFile.getName()));
                return;
            }

            instance.info(String.format("Launching %s ...", launchFile.getName()));

            try {
                JarLoader.load(launchFile);
            } catch (Exception e) {
                instance.exception(e, String.format("Could not load %s", launchFile.getName()));
                return;
            }

            try {
                mainClass = Class.forName(mainClassName);
            } catch (ClassNotFoundException e) {
                instance.warn(String.format("Main class %s not found in %s", mainClassName, launchFile.getName()));
                return;
            } catch (Exception e) {
                instance.exception(e, String.format("Could not load main class %s of %s", mainClassName, launchFile.getName()));
                return;
            }

        }

        if (options.has(main)) {
            String mainClassName = options.valueOf(main);

            instance.info(String.format("Launching main class %s ...", mainClassName));

            try {
                mainClass = Class.forName(mainClassName);
            } catch (ClassNotFoundException e) {
                instance.warn(String.format("Main class %s not found in classpath", mainClassName));
                return;
            } catch (Exception e) {
                instance.exception(e, String.format("Could not load main class %s", mainClassName));
                return;
            }

        }


        Method mainMethod;
        try {
            mainMethod = mainClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            instance.warn(String.format("Could not find main method in main class %s", mainClass.getName()));
            return;
        }

        try {
            List<String> mainArgsList = options.valuesOf(mainArgs);
            String[] invokeArgs = mainArgsList.toArray(new String[0]);

            instance.info(String.format("Invoking %s.%s with %s", mainClass.getName(), mainMethod.getName(), mainArgsList.toString()));

            mainMethod.invoke(null, (Object) invokeArgs);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (IllegalAccessException e) {
            instance.warn(String.format("Main method in main class %s is not accessible", mainClass.getName()));
        }
    }
}
