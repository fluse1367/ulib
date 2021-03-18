package eu.software4you.ulib.impl.database.sql;

import eu.software4you.aether.Dependencies;

class SQLiteDatabaseDepend {
    static {
        Dependencies.depend("{{maven.sqlite}}", "org.sqlite.JDBC");
    }

    static void $() {
        // only used for easy class loading
    }
}
