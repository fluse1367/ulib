package eu.software4you.ulib.core.impl.database;

import eu.software4you.ulib.core.database.Database;
import eu.software4you.ulib.core.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.mysql.MySQLDatabase;
import eu.software4you.ulib.core.impl.database.sql.sqlite.SQLiteDatabase;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.*;

public final class Databases {
    private static final Map<String, Class<? extends Database>> regs = new HashMap<>();

    static {
        regs.put("jdbc:mysql://", MySQLDatabase.class);
        regs.put("jdbc:sqlite:", SQLiteDatabase.class);
    }

    @SneakyThrows
    public static Database prepare(String url, Properties info) {
        for (var en : regs.entrySet()) {
            if (!url.startsWith(en.getKey())) {
                continue;
            }
            Class<? extends Database> clazz = en.getValue();
            try {
                return clazz.getDeclaredConstructor(String.class, Properties.class).newInstance(url, info);
            } catch (NoSuchMethodException ignored) {

            }
        }

        throw new IllegalArgumentException(String.format("Unknown protocol: %s", url));
    }

    @SneakyThrows
    public static SqlDatabase wrap(Connection connection) {
        if (connection.isClosed())
            throw new IllegalStateException("Connection is closed.");
        String url = connection.getMetaData().getURL();

        for (var en : regs.entrySet()) {
            if (!url.startsWith(en.getKey()) || !SqlDatabase.class.isAssignableFrom(en.getValue())) {
                continue;
            }
            Class<? extends SqlDatabase> clazz = (Class<? extends SqlDatabase>) en.getValue();
            try {
                return clazz.getDeclaredConstructor(Connection.class).newInstance(connection);
            } catch (NoSuchMethodException ignored) {

            }
        }

        throw new IllegalArgumentException(String.format("Unknown protocol: %s", url));
    }

    public static eu.software4you.ulib.core.database.sql.SQLiteDatabase prepare(Path path) {
        return (eu.software4you.ulib.core.database.sql.SQLiteDatabase) prepare(String.format("jdbc:sqlite:%s", path.toString()), new Properties());
    }

    @SneakyThrows
    public static eu.software4you.ulib.core.database.sql.MySQLDatabase prepare(String host, int port, String database, String user, String password, String... parameters) {
        StringJoiner params = new StringJoiner("&", "?", "");
        params.setEmptyValue("");
        for (String parameter : parameters) {
            params.add(parameter);
        }
        String url = String.format("jdbc:mysql://%s:%d/%s%s",
                URLEncoder.encode(host, StandardCharsets.UTF_8.toString()),
                port,
                URLEncoder.encode(database, StandardCharsets.UTF_8.toString()),
                params
        );
        Properties info = new Properties();
        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
        return (eu.software4you.ulib.core.database.sql.MySQLDatabase) prepare(url, info);
    }
}
