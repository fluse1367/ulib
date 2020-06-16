package eu.software4you.utils;

import eu.software4you.ulib.ULib;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClassUtils {
    /**
     * @param className the fully qualified name of the desired class.
     * @return true if the class exists or false if it does not.
     */
    public static boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param className the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * @param enumName  the fully qualified name of the desired enum.
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Object getEnumEntry(String enumName, String enumEntry) {
        try {
            return getEnumEntry(Class.forName(enumName), enumEntry);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param enumClass the class of enum
     * @param enumEntry the fully qualified name of the desired entry.
     * @return the {@code Class} object for the class with the
     * specified name, or if the class does not exist null.
     */
    public static Object getEnumEntry(Class<?> enumClass, String enumEntry) {
        try {
            return enumClass.getMethod("valueOf", String.class).invoke(null, enumEntry);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) null, if nothing found
     */
    public static Field findUnderlyingField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        do {
            try {
                return current.getField(fieldName);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @return (1) The field (2) null, if nothing found
     */
    public static Field findUnderlyingDeclaredField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        do {
            try {
                return current.getDeclaredField(fieldName);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz the class to search in
     * @return The collection of fields
     */
    public static Collection<Field> findUnderlyingFields(Class<?> clazz) {
        List<Field> cache = new ArrayList<>();
        Class<?> current = clazz;
        do {
            try {
                cache.addAll(Arrays.asList(current.getFields()));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return cache;
    }

    /**
     * @param clazz the class to search in
     * @return The collection of fields
     */
    public static Collection<Field> findUnderlyingDeclaredFields(Class<?> clazz) {
        List<Field> cache = new ArrayList<>();
        Class<?> current = clazz;
        do {
            try {
                cache.addAll(Arrays.asList(current.getDeclaredFields()));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return cache;
    }

    /**
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) null, if nothing found
     */
    public static Method findUnderlyingMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            try {
                return current.getMethod(methodName, parameterTypes);
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return null;
    }

    /**
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @return (1) The method (2) null, if nothing found
     */
    public static Method findUnderlyingDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            ULib.getInstance().debug(String.format("Searching for %s(%s) in %s", methodName, ArrayUtils.toString(parameterTypes), current.toString()));
            try {
                return current.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        ULib.getInstance().debug(String.format("%s(%s) not found at all", methodName, ArrayUtils.toString(parameterTypes)));
        return null;
    }

    /*public static void generateClassLayer(Class<?> clazz, PrintStream out) {
        java.util.List<String> imports = new java.util.ArrayList<>();
        java.util.LinkedHashMap<String, String> methods = new java.util.LinkedHashMap<>();
        for (java.lang.reflect.Method m : clazz.getDeclaredMethods()) {

            String header = m.toString();
            String[] parts = header.split("\\(")[0].split(" ");
            String returnment = parts[parts.length - 2];
            String name = parts[parts.length - 1];
            String arguments = header.substring(header.indexOf("(") + 1, header.lastIndexOf(")"));
            String[] parameters = arguments.split(",");
            String throwing = header.substring(header.lastIndexOf(")") + 1);


            if (!imports.contains(returnment) && !eu.software4you.reflection.Datatypes.isPrimitiveDatatype(returnment) && !returnment.endsWith("[]") && !returnment.equals(""))
                imports.add(returnment);
            if (returnment.contains(".")) returnment = returnment.substring(returnment.lastIndexOf('.') + 1);
            if (name.contains(".")) name = name.substring(name.lastIndexOf('.') + 1);

            for (int i = 0; i < parameters.length; i++) {
                String parameter = parameters[i];
                if (!imports.contains(parameter) && !eu.software4you.reflection.Datatypes.isPrimitiveDatatype(parameter) && !parameter.endsWith("[]") && !parameter.equals(""))
                    imports.add(parameter);
                String repl = parameter.contains(".") ? parameter.substring(parameter.lastIndexOf('.') + 1) : parameter;
                parameters[i] = repl + (repl.equals("") ? "" : " var" + i);
            }

            if (throwing.startsWith(" throws ")) {
                String[] throwed = throwing.substring(" throws ".length()).split(",");
                for (int i = 0; i < throwed.length; i++) {
                    String th = throwed[i];
                    if (!imports.contains(th)) imports.add(th);
                    throwing = throwing.replace(th, th.substring(th.lastIndexOf('.') + 1));
                }
            }

            header = header.substring(0, header.indexOf(parts[parts.length - 2]));

            String vars = "";
            String params = "";
            for (int i = 0; i < parameters.length; i++)
                if (!parameters[i].equals("")) {
                    params += parameters[i] + (i < parameters.length - 1 ? ", " : "");
                    vars += "var" + i + (i < parameters.length - 1 ? ", " : "");
                }

            header += returnment + " " + name + "(" + params + ")" + throwing;


            //methods.put(header, (returnment.equals("void") ? "" : "return ")+"this.player."+name+"("+vars+")");
            methods.put(header, (returnment.equals("void") ? "" : "return (" + returnment + ") ") + "getMethod(\"" + name + "\").invoke(object" + (vars.equals("") ? "" : ", " + vars) + ")");
        }

        out.println("package " + clazz.getPackage().getName() + ";\n");
        out.println("import eu.software4you.reflection.UniClass;");
        out.println("import eu.software4you.utils.ClassUtils;");
        imports.forEach(im -> out.println("import " + im + ";"));
        out.println("\npublic class " + clazz.getSimpleName() + " extends UniClass {");
        out.println();
        out.println("\tprivate final Object object;");
        out.println();
        out.println("\tpunlic " + clazz.getSimpleName() + "(Object object) {");
        out.println("\t\tsuper(ClassUtils.forName(\"" + clazz.getName() + "\"));");
        out.println("\t\tthis.object = object;");
        out.println("\t}");
        out.println();
        methods.forEach((method, call) -> out.println("\t" + method + " {\n\t\t" + call + ";\n\t}\n"));
        out.println("}");
        if (out != System.out)
            out.close();
    }

    public static void loadClasses(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + file.getPath().replace("\\", "/") + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            try {
                Class c = cl.loadClass(className);
            } catch (Exception e1) {
                //e1.printStackTrace();
            }

        }
    }*/
}
