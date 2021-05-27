package eu.software4you.ulib.impl.database.sql;

import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.impl.database.sql.mysql.MySQLDatabaseDepend;
import eu.software4you.ulib.impl.database.sql.sqlite.SQLiteDatabaseDepend;
import eu.software4you.ulib.inject.Impl;

@Impl(SqlEngine.DriverLoader.class)
final class LegacyDriverLoaderImpl extends SqlEngine.DriverLoader {
    @Override
    protected void load0(SqlEngine.Driver driver) {
        switch (driver) {
            case MySQL:
                MySQLDatabaseDepend.$();
                return;
            case SqLite:
                SQLiteDatabaseDepend.$();
                return;
            default:
                throw new IllegalStateException();
        }
    }
}
