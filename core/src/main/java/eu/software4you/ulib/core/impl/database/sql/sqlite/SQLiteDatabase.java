package eu.software4you.ulib.core.impl.database.sql.sqlite;

import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.Table;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

public final class SQLiteDatabase extends SqlDatabase implements eu.software4you.ulib.core.database.sql.SQLiteDatabase {

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

    @Override
    protected Table createTable(String name, Map<String, Column<?>> columns) {
        return new SQLiteTable(this, name, columns);
    }

    @Override
    protected String autoIncrementKeyword() {
        return "autoincrement";
    }

    @Override
    protected boolean applyIndexBeforeAI() {
        return true;
    }
}
