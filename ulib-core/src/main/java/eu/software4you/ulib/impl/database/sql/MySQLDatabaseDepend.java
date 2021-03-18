package eu.software4you.ulib.impl.database.sql;

import eu.software4you.aether.Dependencies;

class MySQLDatabaseDepend {
    static {
        Dependencies.depend("{{maven.mysql}}", "com.mysql.cj.jdbc.Driver");
    }

    static void $() {
        // only used for easy class loading
    }
}
