package eu.software4you.configuration;

import org.apache.commons.lang.Validate;
import ulib.ported.org.bukkit.configuration.Configuration;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigurationWrapper {
    private ConfigurationSection section;

    public ConfigurationWrapper(ConfigurationSection section) {
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

    public ConfigurationWrapper sub(String s) {
        return section().isConfigurationSection(s) ? new ConfigurationWrapper(section(s)) : null;
    }

    public ConfigurationWrapper subAndCreate(String s) {
        return new ConfigurationWrapper(sectionAndCreate(s));
    }

    /* primitives */

    public boolean bool(String path) {
        return section.getBoolean(path);
    }

    public boolean bool(String path, boolean def) {
        return section.getBoolean(path, def);
    }

    public int integer(String path) {
        return section.getInt(path);
    }

    public int integer(String path, int def) {
        return section.getInt(path, def);
    }

    public float floatDec(String path) {
        return (float) doubleDec(path);
    }

    public float floatDec(String path, float def) {
        return (float) doubleDec(path, def);
    }

    public double doubleDec(String path, double def) {
        return section.getDouble(path, def);
    }

    public double doubleDec(String path) {
        return section.getDouble(path);
    }

    public long longNum(String path) {
        return section.getLong(path);
    }

    public long longNum(String path, long def) {
        return section.getLong(path, def);
    }

    public String string(String path, Object def, Object... replacements) {
        return get(String.class, path, def != null ? def.toString() : null, replacements);
    }

    public List<String> stringList(String path, List<String> def, Object... replacements) {
        return list(String.class, path, def, replacements);
    }

    public <T> T get(Class<T> clazz, String path, T def, Object... replacements) {
        Object val = clazz.equals(String.class) ? section.getString(path, def != null ? def.toString() : null) : section.get(path, def);
        if (val == null)
            return def;
        if (val instanceof String)
            val = String.format((String) val, replacements);
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

    public <T> List<T> list(Class<T> clazz, String path, List<T> def, Object... replacements) {
        List<T> list = new ArrayList<>();
        List<?> myList = section.getList(path, def);
        if (myList == null)
            return def;
        Iterator<?> iterator = myList.iterator();
        while (iterator.hasNext()) {
            Object val = iterator.next();
            isInstance(val, clazz, path);
            if (val instanceof String)
                val = String.format((String) val, replacements);
            list.add((T) val);
        }
        return list;
    }

    private void isInstance(Object val, Class<?> clazz, String path) {
        Validate.isTrue(clazz.isInstance(val), val.getClass().getName() + " is not an instance of " + clazz.getName() + " (" + path + ")");
    }
}
