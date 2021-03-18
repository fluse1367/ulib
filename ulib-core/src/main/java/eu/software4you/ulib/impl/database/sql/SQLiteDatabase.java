package eu.software4you.ulib.impl.database.sql;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Properties;

public final class SQLiteDatabase extends SqlDatabase implements eu.software4you.database.sql.SQLiteDatabase {

    @Getter
    private final Path path;

    @SneakyThrows
    public SQLiteDatabase(Connection connection) {
        super(connection);
        path = determine(connection.getMetaData().getURL());
        SQLiteDatabaseDepend.$();
    }

    public SQLiteDatabase(String url, Properties info) {
        super(url, info);
        path = determine(url);
        SQLiteDatabaseDepend.$();
    }

    private static Path determine(String url) {
        if (!url.startsWith("jdbc:sqlite:"))
            throw new IllegalArgumentException(String.format("Unknown protocol: %s", url));
        return Paths.get(url.substring(12));
    }
}
