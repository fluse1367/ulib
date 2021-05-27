package eu.software4you.ulib.impl.database.sql.sqlite;

import eu.software4you.aether.Dependencies;

public class SQLiteDatabaseDepend {
    static {
        Dependencies.depend("{{maven.sqlite}}", "org.sqlite.JDBC");
    }

    public static void $() {
        // only used for easy class loading
    }
}
