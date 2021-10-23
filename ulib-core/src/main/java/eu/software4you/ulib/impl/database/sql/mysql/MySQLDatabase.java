package eu.software4you.ulib.impl.database.sql.mysql;

import eu.software4you.database.sql.Column;
import eu.software4you.ulib.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.impl.database.sql.Table;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

public final class MySQLDatabase extends SqlDatabase implements eu.software4you.database.sql.MySQLDatabase {
    public MySQLDatabase(Connection connection) {
        super(connection);
        MySQLDatabaseDepend.$();
    }

    public MySQLDatabase(String url, Properties info) {
        super(url, info);
        MySQLDatabaseDepend.$();
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
}
