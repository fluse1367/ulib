package eu.software4you.ulib.core.impl.database.sql.query;

import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.Table;
import org.jetbrains.annotations.NotNull;

public final class QueryStart implements eu.software4you.ulib.core.database.sql.query.QueryStart {
    private final Metadata meta;

    public QueryStart(SqlDatabase sql, Table table, String operand) {
        this.meta = new Metadata(sql, new StringBuilder(String.format("%s `%s`", operand, table.getName())));
    }

    @Override
    public @NotNull Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull Column<?> column) {
        return where(column.getName());
    }

    @Override
    public @NotNull Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull String column) {
        return new Condition<>(meta, column, Where::new);
    }

    @Override
    public @NotNull Where whereRaw(@NotNull String condition) {
        return new Where(meta, condition);
    }
}
