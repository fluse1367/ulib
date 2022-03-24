package eu.software4you.ulib.loader.launch;

import joptsimple.*;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.*;

final class Launcher {

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
            System.err.println(e.getMessage());
            parser.printHelpOn(System.out);
            return;
        }

        if (!options.hasOptions()) {
            System.out.println("This is a library, so it's not designed to run stand-alone.");
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

        final Class<?> mainClass;
        if (options.has(launch)) {
            String launchFilePath = options.valueOf(launch);
            File launchFile = new File(launchFilePath);
            if (!launchFile.exists()) {
                System.err.printf("Cannot launch specified file: File %s does not exist.%n", launchFilePath);
                return;
            }

            JarFile jar = new JarFile(launchFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                System.err.printf("%s does not have a manifest.%n", launchFile.getName());
                return;
            }

            Attributes mainAttr = manifest.getMainAttributes();

            String mainClassName = mainAttr.getValue("Main-Class");

            if (mainClassName == null) {
                System.err.printf("%s does not have a main class.%n", launchFile.getName());
                return;
            }

            System.err.printf("Launching %s ...%n", launchFile.getName());

            try {
                var cl = new URLClassLoader(new URL[]{launchFile.toURI().toURL()}, Launcher.class.getClassLoader());
                mainClass = Class.forName(mainClassName, true, cl);
            } catch (ClassNotFoundException e) {
                System.err.printf("Main class %s not found in %s%n", mainClassName, launchFile.getName());
                return;
            } catch (Exception e) {
                System.err.printf("Could not load main class %s of %s%n",
                        mainClassName, launchFile.getName());
                e.printStackTrace();
                return;
            }

        } else { // implicit option.has(main) == true
            String mainClassName = options.valueOf(main);

            System.out.printf("Launching main class %s ...%n", mainClassName);

            try {
                mainClass = Class.forName(mainClassName);
            } catch (ClassNotFoundException e) {
                System.err.printf("Main class %s not found in classpath%n", mainClassName);
                return;
            } catch (Exception e) {
                System.err.printf("Could not load main class %s%n", mainClassName);
                e.printStackTrace();
                return;
            }
        }


        Method mainMethod;
        try {
            mainMethod = mainClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            System.err.printf("Could not find main method in main class %s%n", mainClass.getName());
            return;
        }

        try {
            List<String> mainArgsList = options.valuesOf(mainArgs);
            String[] invokeArgs = mainArgsList.toArray(new String[0]);

            System.out.printf("Invoking %s.%s with %s%n", mainClass.getName(), mainMethod.getName(), mainArgsList);

            mainMethod.invoke(null, (Object) invokeArgs);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.printf("Main method in main class %s is not accessible%n", mainClass.getName());
        }
    }
}
