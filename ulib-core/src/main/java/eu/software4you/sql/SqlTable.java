package eu.software4you.sql;

import java.sql.SQLException;
import java.util.*;

/**
 * Representation of an sql table
 */
public class SqlTable {
    private final SqlEngine engine;
    private final String tableName;
    private final List<Key> defaultKeys = new ArrayList<>();
    private final HashMap<String, Key> defaultKeysMap = new HashMap<>();
    private Key primaryKey = null;

    SqlTable(SqlEngine engine, String tableName) {
        this.engine = engine;
        this.tableName = tableName;
    }

    /**
     * Creates the Table
     *
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void create() throws SQLException {
        if (engine.disableAutomaticParameterizedQueries) {
            // INSECURE
            String cmd = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (";
            for (Key key : defaultKeys) {
                cmd += "`" + key + "` " + key.type;
                if (key.notNull) {
                    cmd += " NOT NULL";
                }
                if (key.defaultValue != null) {
                    cmd += " DEFAULT ";
                    if (key.type == Key.KeyType.String || key.type == Key.KeyType.BigString) {
                        cmd += String.format("'%s'", key.defaultValue);
                    } else {
                        cmd += key.defaultValue;
                    }
                }
                if (primaryKey != null && primaryKey.toString().equals(key.toString())) cmd += " PRIMARY KEY";
                cmd += ", ";
            }
            if (cmd.endsWith(", ")) cmd = cmd.substring(0, cmd.length() - 2);
            cmd += ")";
            engine.execute(cmd);
            return;
        }

        List<Object> params = new ArrayList<>(Collections.singletonList(tableName));
        String cmd = "create table if not exists ? (";

        for (Key key : defaultKeys) {

            cmd += "? " + key.type;
            params.add(key.key);
            if (key.notNull) {
                cmd += " not null";
            }
            if (key.defaultValue != null) {
                cmd += " default ?";
                params.add(key.defaultValue);
            }
            if (primaryKey != null && primaryKey.toString().equals(key.toString()))
                cmd += " primary key";
            cmd += ", ";
        }

        if (cmd.endsWith(", "))
            cmd = cmd.substring(0, cmd.length() - 2);
        cmd += ")";

        engine.execute(cmd, params);
    }

    /**
     * Deletes the table
     *
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void drop() throws SQLException {
        engine.execute("DROP TABLE ?", name());
    }

    /**
     * Adds a default key
     *
     * @param key the key
     * @throws UnsupportedOperationException if the key is marked as primary, but another is marked as primary too. Will not interrupt regular work.
     */
    public void addDefaultKey(Key key) {
        if (!defaultKeys.contains(key)) {
            defaultKeys.add(key);
            defaultKeysMap.put(key.toString(), key);
            if (key.primary) {
                if (primaryKey != null)
                    throw new UnsupportedOperationException("Primary key already set");
                setPrimaryKey(key);
            }
        }
    }

    /**
     * Gets the default keys
     *
     * @return the default keys
     */
    public Map<String, Key> getDefaultKeys() {
        return Collections.unmodifiableMap(defaultKeysMap);
    }

    public List<Key> getDefaultKeysList() {
        return Collections.unmodifiableList(defaultKeys);
    }

    /**
     * Gets the default values for the keys
     *
     * @return Object array of default values.
     * If no default value given, null wil be inserted
     */
    public Object[] getDefaultValues() {
        Object[] obj = new Object[this.defaultKeys.size()];
        for (int i = 0; i < this.defaultKeys.size(); i++) {
            Key key = defaultKeys.get(i);
            obj[i] = key.defaultValue;
        }
        return obj;
    }

    /**
     * Sets the primary key
     *
     * @param key the key
     */
    public void setPrimaryKey(Key key) {
        this.primaryKey = key;
    }

    /**
     * Sets the primary key
     *
     * @param key the key name
     */
    public void setPrimaryKey(String key) {
        Key k = getDefaultKeys().get(key);
        if (k == null)
            throw new Error("Key not found");
        setPrimaryKey(k);
    }

    /**
     * Gets the name of the table
     *
     * @return name of table
     * @see #toString()
     */
    public String name() {
        return this.tableName;
    }

    public String getName() {
        return tableName;
    }

    /**
     * Get's the name of the table
     *
     * @return name of table
     * @see #name()
     */
    @Override
    public String toString() {
        return name();
    }

    /**
     * Representation for a key (column) in a sql table
     */
    public static class Key {
        private final String key;
        private final KeyType type;
        private Object defaultValue = null;
        private boolean notNull = false;
        private boolean primary = false;

        /**
         * @param key  name of key
         * @param type type of key
         */
        public Key(String key, KeyType type) {
            this.key = key;
            this.type = type;
        }

        /**
         * Gets the key's name
         *
         * @return key name
         */
        @Override
        public String toString() {
            return this.key;
        }

        /**
         * Makes the key {@code NOT NULL}
         *
         * @return the instance
         */
        public Key notNull() {
            notNull = true;
            return this;
        }

        /**
         * Sets the key's default value
         *
         * @param defaultValue the default sql value
         * @return the instance
         */
        public Key defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Set's the key's size
         *
         * @param size the size of the key value
         * @return the instance
         */
        public Key size(int size) {
            type.size = size;
            return this;
        }

        /**
         * Make the key the primary key.
         * WARNING: if more than one key is primary an {@link UnsupportedOperationException} will be thrown when adding the key to a table
         *
         * @return the instance
         */
        public Key primary() {
            primary = true;
            return this;
        }

        /**
         * Representation of different key types
         */
        public enum KeyType {
            /**
             * SQL Data Type {@code VARCHAR} (see {@link Character}, {@link String}), {@link CharSequence} with a fixed length (default 255)
             */
            FixedCharacter("CHAR", 255),
            /**
             * SQL Data Type {@code VARCHAR} (see {@link Character}, {@link String}), {@link CharSequence} with a length limit (default 255)
             */
            VariableCharacter("VARCHAR", 255),
            /**
             * SQL Data Type {@code TEXT} (see {@link String})
             */
            String("TEXT"),
            /**
             * SQL Data Type {@code TEXT} (see {@link String})
             */
            BigString("LONGTEXT"),
            /**
             * SQL Data Type {@code BOOLEAN} (see {@link Boolean})
             */
            Boolean("BOOLEAN"),
            /**
             * SQL Data Type {@code INT} (see {@link Integer})
             */
            Integer("INT"),
            /**
             * SQL Data Type {@code BIGINT} (see {@link Long})
             */
            BigInteger("BIGINT"),
            /**
             * SQL Data Type {@code DECIMAL}
             */
            Decimal("DECIMAL"),
            /**
             * SQL Data Type {@code FLOAT} (see {@link Float})
             */
            Float("FLOAT"),
            /**
             * SQL Data Type {@code DOUBLE} (see {@link Double})
             */
            Double("DOUBLE"),

            ;


            private final String id;
            public int size;

            KeyType(String id) {
                this(id, -1);
            }

            KeyType(String id, int size) {
                this.id = id;
                this.size = size;
            }

            /**
             * @return query part
             */
            @Override
            public String toString() {
                String ret = this.id;
                if (size > 0)
                    ret += "(" + this.size + ")";
                return ret;
            }
        }

    }
}