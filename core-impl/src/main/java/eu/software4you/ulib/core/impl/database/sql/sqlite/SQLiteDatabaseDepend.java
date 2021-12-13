package eu.software4you.ulib.core.impl.database.sql.sqlite;

import eu.software4you.ulib.core.api.dependencies.Dependencies;

public class SQLiteDatabaseDepend {
    static {
        Dependencies.depend("{{maven.sqlite}}", "org.sqlite.JDBC");
    }

    public static void $() {
        // only used for easy class loading
    }
}
