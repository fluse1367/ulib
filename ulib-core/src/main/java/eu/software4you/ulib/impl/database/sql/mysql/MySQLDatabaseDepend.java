package eu.software4you.ulib.impl.database.sql.mysql;

import eu.software4you.dependencies.Dependencies;

public class MySQLDatabaseDepend {
    static {
        Dependencies.depend("{{maven.mysql}}", "com.mysql.cj.jdbc.Driver");
    }

    public static void $() {
        // only used for easy class loading
    }
}
