package eu.software4you.ulib.core.impl.database.sql.mysql;

import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.Table;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

public final class MySQLDatabase extends SqlDatabase implements eu.software4you.ulib.core.database.sql.MySQLDatabase {
    public MySQLDatabase(Connection connection) {
        super(connection);
    }

    public MySQLDatabase(String url, Properties info) {
        super(url, info);
    }

    @Override
    protected Table createTable(String name, Map<String, Column<?>> columns) {
        return new MySQLTable(this, name, columns);
    }

    @Override
    protected String autoIncrementKeyword() {
        return "auto_increment";
    }

    @Override
    protected boolean applyIndexBeforeAI() {
        return false;
    }

    @Override
    protected String lastInsertId() {
        return "last_insert_id";
    }

    @Override
    protected String driverCoordinates() {
        return "{{maven.mysql}}";
    }
}
