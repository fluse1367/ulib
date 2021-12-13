package eu.software4you.ulib.core.impl.database.sql;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.impl.database.sql.mysql.MySQLDatabaseDepend;
import eu.software4you.ulib.core.impl.database.sql.sqlite.SQLiteDatabaseDepend;

final class LegacyDriverLoaderImpl extends SqlEngine.DriverLoader {
    @Override
    protected void load0(SqlEngine.Driver driver) {
        switch (driver) {
            case MySQL -> MySQLDatabaseDepend.$();
            case SqLite -> SQLiteDatabaseDepend.$();
            default -> throw new IllegalStateException();
        }
    }
}
