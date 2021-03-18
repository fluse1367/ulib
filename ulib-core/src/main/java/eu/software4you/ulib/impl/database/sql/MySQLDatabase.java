package eu.software4you.ulib.impl.database.sql;

import java.sql.Connection;
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
}
