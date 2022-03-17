package eu.software4you.ulib.core.impl.database.sql.query;

import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.Table;
import org.jetbrains.annotations.NotNull;

public class Query extends QueryEndpoint implements eu.software4you.ulib.core.database.sql.query.Query {

    public Query(SqlDatabase sql, Table table, String operand) {
        super(new Metadata(sql, new StringBuilder(String.format("%s `%s`", operand, table.getName()))));
    }

    @Override
    public Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull Column<?> column) {
        return where(column.getName());
    }

    @Override
    public Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull String column) {
        return new Condition<>(meta, column, Where::new);
    }

    @Override
    public Where whereRaw(@NotNull String condition) {
        return new Where(meta, condition);
    }
}
