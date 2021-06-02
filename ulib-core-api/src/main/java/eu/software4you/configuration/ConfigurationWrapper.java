package eu.software4you.configuration;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ulib.ported.org.bukkit.configuration.Configuration;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for the {@link ConfigurationSection} with some useful shortcuts.
 */
public class ConfigurationWrapper {
    private ConfigurationSection section;

    /**
     * The default constructor.
     *
     * @param section the configuration section to wrap
     */
    public ConfigurationWrapper(@Nullable ConfigurationSection section) {
        this.section = section;
    }

    /* sections */

    /**
     * Sets the current section of this wrapper.
     * This <b>does not</b> reflect sub-instances.
     *
     * @param section the section to set
     */
    public void setSection(@Nullable ConfigurationSection section) {
        this.section = section;
    }

    /**
     * Returns a already existing sub-section.
     *
     * @param key the key
     * @return the sub-section, or {@code null} if the key does not exist
     */
    @Nullable
    public ConfigurationSection section(@NotNull String key) {
        return section.getConfigurationSection(key);
    }

    /**
     * Returns a sub-section, and creates it if it does not already exist.
     *
     * @param key the key
     * @return the sub-section
     */
    @NotNull
    public ConfigurationSection sectionAndCreate(@NotNull String key) {
        if (!section.isConfigurationSection(key))
            return section.createSection(key);
        //noinspection ConstantConditions
        return section.getConfigurationSection(key);
    }

    /**
     * Returns the current section.
     *
     * @return the current section
     */
    @Nullable
    public ConfigurationSection section() {
        return section;
    }

    /**
     * Returns the current section's root.
     *
     * @return the current section's root
     * @see Configuration#getRoot()
     */
    public Configuration root() {
        return section.getRoot();
    }

    /**
     * Gets a already existing sub-section and wraps it.
     *
     * @param key the key
     * @return the wrapped section, or {@code null}, if {@code key} does not exist
     * @see #section(String)
     */
    @Nullable
    public ConfigurationWrapper sub(@NotNull String key) {
        return section().isConfigurationSection(key) ? new ConfigurationWrapper(section(key)) : null;
    }

    /**
     * Gets a sub-section, and creates it if it does not already exist, and wraps it.
     *
     * @param key the key
     * @return the wrapped sub-section
     * @see #sectionAndCreate(String)
     */
    @NotNull
    public ConfigurationWrapper subAndCreate(@NotNull String key) {
        return new ConfigurationWrapper(sectionAndCreate(key));
    }

    /* primitives */

    /**
     * Reads a boolean. Defaults to {@code false}.
     *
     * @param key the key
     * @return the read boolean, or {@code false} if {@code key} does not exist
     * @see ConfigurationSection#getBoolean(String)
     */
    public boolean bool(@NotNull String key) {
        return section.getBoolean(key);
    }

    /**
     * Reads a boolean.
     *
     * @param key the key
     * @param def default value that will be returned if {@code key} does not exist
     * @return the boolean
     * @see ConfigurationSection#getBoolean(String, boolean)
     */
    public boolean bool(@NotNull String key, boolean def) {
        return section.getBoolean(key, def);
    }

    /**
     * Reads an integer. Defaults to {@code -1}.
     *
     * @param key the key
     * @return the read integer, or {@code -1} if {@code key} does not exist
     * @see ConfigurationSection#getInt(String)
     */
    public int integer(@NotNull String key) {
        return section.getInt(key);
    }

    /**
     * Reads an integer.
     *
     * @param key the key
     * @param def default value that will be returned if {@code key} does not exist
     * @return the integer
     * @see ConfigurationSection#getInt(String, int)
     */
    public int integer(@NotNull String key, int def) {
        return section.getInt(key, def);
    }

    /**
     * Reads a float. Defaults to {@code -1}.
     *
     * @param key the key
     * @return the read float, or {@code -1} if {@code key} does not exist
     * @see ConfigurationSection#getDouble(String)
     */
    public float floatDec(@NotNull String key) {
        return (float) doubleDec(key);
    }

    /**
     * Reads a float.
     *
     * @param key the key
     * @param def default value that will be returned if {@code key} does not exist
     * @return the float
     * @see ConfigurationSection#getDouble(String, double)
     */
    public float floatDec(@NotNull String key, float def) {
        return (float) doubleDec(key, def);
    }

    /**
     * Reads a double. Defaults to {@code -1}.
     *
     * @param key the key
     * @return the read double, or {@code -1} if {@code key} does not exist
     * @see ConfigurationSection#getDouble(String)
     */
    public double doubleDec(@NotNull String key) {
        return section.getDouble(key);
    }

    /**
     * Reads a double.
     *
     * @param key the key
     * @param def default value that will be returned if {@code key} does not exist
     * @return the double
     * @see ConfigurationSection#getDouble(String, double)
     */
    public double doubleDec(@NotNull String key, double def) {
        return section.getDouble(key, def);
    }

    /**
     * Reads a long. Defaults to {@code -1}.
     *
     * @param key the key
     * @return the read long, or {@code -1} if {@code key} does not exist
     * @see ConfigurationSection#getLong(String)
     */
    public long longNum(@NotNull String key) {
        return section.getLong(key);
    }

    /**
     * Reads a lang.
     *
     * @param key the key
     * @param def default value that will be returned if {@code key} does not exist
     * @return the long
     * @see ConfigurationSection#getLong(String, long)
     */
    public long longNum(@NotNull String key, long def) {
        return section.getLong(key, def);
    }

    /**
     * Reads a String and processes it with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param key          the key
     * @param replacements the replacements
     * @return the processed string, or {@code null} if {@code key} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    public String string(@NotNull String key, Object... replacements) {
        return get(String.class, key, replacements);
    }

    /**
     * Reads a String and processes it with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param key          the key
     * @param def          default value that will be returned if {@code key} does not exist
     * @param replacements the replacements
     * @return the processed string, or {@code def} if {@code key} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    public String string(@NotNull String key, @Nullable String def, Object... replacements) {
        return get(String.class, key, def, replacements);
    }

    /**
     * Reads a String type List and processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given.
     *
     * @param key          the key
     * @param replacements the replacements
     * @return the processed string list, or {@code null} if {@code key} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    public List<String> stringList(@NotNull String key, Object... replacements) {
        return list(String.class, key, null, replacements);
    }

    /**
     * Reads a String type List and processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given.
     *
     * @param key          the key
     * @param replacements the replacements
     * @param def          default value that will be returned if {@code key} does not exist
     * @return the processed string list, or {@code def} if {@code key} does not exist
     * @see String#format(String, Object...)
     */
    @Nullable
    public List<String> stringList(@NotNull String key, @Nullable List<String> def, Object... replacements) {
        return list(String.class, key, def, replacements);
    }

    /**
     * Reads a value and tries to convert it to the given type. If the value is a String type, it is processed
     * with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param key          the key
     * @param replacements the replacements
     * @param <T>          the type
     * @return the converted value, or {@code null} if {@code key} does not exist
     * @throws NumberFormatException    if {@code <T>} is a number type but the read value cannot be parsed to it
     * @throws IllegalArgumentException if read value is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    public <T> T get(Class<T> clazz, String key, Object... replacements) {
        return get(clazz, key, null, replacements);
    }

    /**
     * Reads a value and tries to convert it to the given type. If the value is a String type, it is processed
     * with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param key          the key
     * @param def          default value that will be returned if {@code key} does not exist
     * @param replacements the replacements
     * @param <T>          the type
     * @return the converted value, or {@code def} if {@code key} does not exist
     * @throws NumberFormatException    if {@code <T>} is a number type but the read value cannot be parsed to it
     * @throws IllegalArgumentException if read value is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    public <T> T get(Class<T> clazz, String key, T def, Object... replacements) {
        Object val = clazz.equals(String.class) ? section.getString(key, def != null ? def.toString() : null) : section.get(key, def);
        if (val == null)
            return def;
        if (val instanceof String)
            val = replacements.length > 0 ? String.format((String) val, replacements) : (String) val;
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
        isInstance(val, clazz, key);
        if (clazz.isInstance(val))
            return (T) val;
        throw new IllegalStateException("This point should not be reached. Something is very wrong.");
    }

    /**
     * Reads a list and tries to convert each entry to the given type. If the entries are a String type,
     * they are processed with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param key          the key
     * @param replacements the replacements
     * @param <T>          the type
     * @return the list containing the converted values, or {@code null} if {@code key} does not exist
     * @throws IllegalArgumentException if a entry is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    public <T> List<T> list(Class<T> clazz, String key, Object... replacements) {
        return list(clazz, key, null, replacements);
    }

    /**
     * Reads a list and tries to convert each entry to the given type. If the entries are a String type,
     * they are processed with {@link String#format(String, Object...)} if {@code replacements} are given.
     *
     * @param clazz        the type class
     * @param key          the key
     * @param def          default value that will be returned if {@code key} does not exist
     * @param replacements the replacements
     * @param <T>          the type
     * @return the list containing the converted values, or {@code def} if {@code key} does not exist
     * @throws IllegalArgumentException if a entry is not of type {@code <T>}
     * @see String#format(String, Object...)
     */
    public <T> List<T> list(Class<T> clazz, String key, List<T> def, Object... replacements) {
        List<T> list = new ArrayList<>();
        List<?> myList = section.getList(key, def);
        if (myList == null)
            return def;
        for (Object val : myList) {
            isInstance(val, clazz, key);
            if (val instanceof String)
                val = replacements.length > 0 ? String.format((String) val, replacements) : (String) val;
            list.add((T) val);
        }
        return list;
    }

    private void isInstance(Object val, Class<?> clazz, String key) {
        Validate.isTrue(clazz.isInstance(val),
                val.getClass().getName() + " is not of type " + clazz.getName() + " (key: " + key + ")");
    }
}
