package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

public interface SetQuery extends Query {
    SetQuery set(Column<?> column, Object to);

    SetQuery set(String column, Object to);
}
