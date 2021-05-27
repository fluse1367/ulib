package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;
import org.jetbrains.annotations.NotNull;

class Query extends QueryEndpoint implements eu.software4you.database.sql.query.Query {

    Query(SqlDatabase sql, Table table, String operand) {
        super(new Metadata(sql, new StringBuilder(String.format("%s `%s`", operand, table.getName()))));
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(@NotNull Column<?> column) {
        return where(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(@NotNull String column) {
        return new Condition<>(meta, column, Where::new);
    }

    @Override
    public Where whereRaw(@NotNull String condition) {
        return new Where(meta, condition);
    }
}
