package eu.software4you.ulib.impl.database.sql;

import eu.software4you.aether.Dependencies;

import java.sql.Connection;
import java.util.Properties;

public final class MySQLDatabase extends SqlDatabase implements eu.software4you.database.sql.MySQLDatabase {
    static {
        Dependencies.depend("mysql:mysql-connector-java:8.0.23", "com.mysql.cj.jdbc.Driver");
    }

    public MySQLDatabase(Connection connection) {
        super(connection);
    }

    public MySQLDatabase(String url, Properties info) {
        super(url, info);
    }
}
