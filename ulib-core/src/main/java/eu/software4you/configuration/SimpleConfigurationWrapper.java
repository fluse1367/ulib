package eu.software4you.configuration;

import org.apache.commons.lang.Validate;
import ported.org.bukkit.configuration.Configuration;
import ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleConfigurationWrapper {
    private ConfigurationSection section;

    public SimpleConfigurationWrapper(ConfigurationSection section) {
        this.section = section;
    }

    /* sections */

    public void setSection(ConfigurationSection section) {
        this.section = section;
    }

    public ConfigurationSection section(String s) {
        return section.getConfigurationSection(s);
    }

    public ConfigurationSection sectionAndCreate(String s) {
        if (!section.isConfigurationSection(s))
            return section.createSection(s);
        return section.getConfigurationSection(s);
    }

    public ConfigurationSection section() {
        return section;
    }

    public Configuration root() {
        return section.getRoot();
    }

    public SimpleConfigurationWrapper sub(String s) {
        return section().isConfigurationSection(s) ? new SimpleConfigurationWrapper(section(s)) : null;
    }

    public SimpleConfigurationWrapper subAndCreate(String s) {
        return new SimpleConfigurationWrapper(sectionAndCreate(s));
    }

    /* primitives */

    public boolean bool(String path) {
        return get(Boolean.class, path, false);
    }

    public int integer(String path) {
        return get(Integer.class, path, -1);
    }

    public float floatDec(String path) {
        return get(Float.class, path, -1F);
    }

    public double doubleDec(String path) {
        return get(Double.class, path, -1D);
    }

    public long longNum(String path) {
        return get(Long.class, path, -1L);
    }

    /* complex */

    @Deprecated
    public String string(String path, String... replacements) {
        return string(path, null, replacements);
    }

    public String string(String path, Object def, String... replacements) {
        return get(String.class, path, def != null ? def.toString() : null, replacements);
    }

    @Deprecated
    public List<String> stringList(String path, String... replacements) {
        return stringList(path, new ArrayList<>(), replacements);
    }

    public List<String> stringList(String path, List<String> def, String... replacements) {
        return list(String.class, path, def, replacements);
    }

    @Deprecated
    public <T> T get(Class<T> clazz, String path, String... replacements) {
        return get(clazz, path, null, replacements);
    }

    public <T> T get(Class<T> clazz, String path, T def, String... replacements) {
        Object val = clazz.equals(String.class) ? section.getString(path, def != null ? def.toString() : null) : section.get(path, def);
        if (val == null)
            return def;
        if (val instanceof String)
            val = replace((String) val, replacements);
        else if (val instanceof Number) {
            if (clazz == Integer.class && !(val instanceof Integer))
                return clazz.cast(Integer.parseInt(String.valueOf(val)));
            if (clazz == Float.class && !(val instanceof Float))
                return clazz.cast(Float.parseFloat(String.valueOf(val)));
            if (clazz == Double.class && !(val instanceof Double))
                return clazz.cast(Double.parseDouble(String.valueOf(val)));
            if (clazz == Long.class && !(val instanceof Long))
                return clazz.cast(Long.parseLong(String.valueOf(val)));
        }
        isInstance(val, clazz, path);
        if (clazz.isInstance(val))
            return (T) val;
        // TODO: throw IllegalArgumentException?
        return def;
    }

    @Deprecated
    public <T> List<T> list(Class<T> clazz, String path, String... replacements) {
        return list(clazz, path, null, replacements);
    }

    public <T> List<T> list(Class<T> clazz, String path, List<T> def, String... replacements) {
        List<T> list = new ArrayList<>();
        List<?> myList = section.getList(path, def);
        if (myList == null)
            return def;
        Iterator<?> iterator = myList.iterator();
        while (iterator.hasNext()) {
            Object val = iterator.next();
            isInstance(val, clazz, path);
            if (val instanceof String)
                val = replace((String) val, replacements);
            list.add((T) val);
        }
        return list;
    }

    /* util */

    private String replace(String s, String... replacements) {
        for (int i = replacements.length - 1; i >= 0; i--) {
            s = s.replace("%" + i, replacements[i]);
        }
        return s;
    }

    private void isInstance(Object val, Class<?> clazz, String path) {
        Validate.isTrue(clazz.isInstance(val), val.getClass().getName() + " is not an instance of " + clazz.getName() + " (" + path + ")");
    }
}
