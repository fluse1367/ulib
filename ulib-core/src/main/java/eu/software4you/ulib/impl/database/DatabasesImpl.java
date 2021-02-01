package eu.software4you.ulib.impl.database;

import eu.software4you.database.Database;
import eu.software4you.database.Databases;
import eu.software4you.database.sql.SqlDatabase;
import eu.software4you.ulib.impl.database.sql.MySQLDatabase;
import eu.software4you.ulib.impl.database.sql.SQLiteDatabase;
import eu.software4you.ulib.inject.Impl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

@Impl(Databases.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class DatabasesImpl extends Databases {
    private static final Map<String, Class<? extends Database>> regs = new HashMap<>();

    static {
        regs.put("jdbc:mysql://", MySQLDatabase.class);
        regs.put("jdbc:sqlite:", SQLiteDatabase.class);
    }

    @SneakyThrows
    @Override
    protected Database prepare0(String url, Properties info) {
        for (val en : regs.entrySet()) {
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
    @Override
    protected SqlDatabase wrap0(Connection connection) {
        if (connection.isClosed())
            throw new IllegalStateException("Connection is closed.");
        String url = connection.getMetaData().getURL();

        for (val en : regs.entrySet()) {
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

    @Override
    protected eu.software4you.database.sql.SQLiteDatabase prepare0(File file) {
        return (eu.software4you.database.sql.SQLiteDatabase) prepare0(String.format("jdbc:sqlite:%s", file.getPath()), new Properties());
    }

    @SneakyThrows
    @Override
    protected eu.software4you.database.sql.MySQLDatabase prepare0(String host, int port, String database, String user, String password, String... parameters) {
        StringJoiner params = new StringJoiner("&", "?", "");
        params.setEmptyValue("");
        for (String parameter : parameters) {
            params.add(parameter);
        }
        String url = String.format("jdbc:mysql://%s:%d/%s%s",
                URLEncoder.encode(host, StandardCharsets.UTF_8.toString()),
                port,
                URLEncoder.encode(database, StandardCharsets.UTF_8.toString()),
                params.toString()
        );
        Properties info = new Properties();
        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
        return (eu.software4you.database.sql.MySQLDatabase) prepare0(url, info);
    }
}
