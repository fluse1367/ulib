package eu.software4you.ulib.impl.database.sql;

import eu.software4you.aether.Dependencies;
import eu.software4you.aether.Repository;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Properties;

public final class SQLiteDatabase extends SqlDatabase implements eu.software4you.database.sql.SQLiteDatabase {
    static {
        Dependencies.depend("org.xerial:sqlite-jdbc:3.25.2", "org.sqlite.JDBC", Repository.MAVEN_CENTRAL);
    }

    @Getter
    private final Path path;

    @SneakyThrows
    public SQLiteDatabase(Connection connection) {
        super(connection);
        path = determine(connection.getMetaData().getURL());
    }

    public SQLiteDatabase(String url, Properties info) {
        super(url, info);
        path = determine(url);
    }

    private static Path determine(String url) {
        if (!url.startsWith("jdbc:sqlite:"))
            throw new IllegalArgumentException(String.format("Unknown protocol: %s", url));
        return Paths.get(url.substring(12));
    }
}
