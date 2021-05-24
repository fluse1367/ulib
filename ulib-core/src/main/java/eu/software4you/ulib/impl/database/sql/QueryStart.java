package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;

final class QueryStart implements eu.software4you.database.sql.query.QueryStart {
    private final SqlDatabase sql;
    private final Table table;
    private final StringBuilder query;

    QueryStart(SqlDatabase sql, Table table, String operand) {
        this.sql = sql;
        this.table = table;
        this.query = new StringBuilder(String.format("%s `%s`", operand, table.getName()));
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(Column<?> column) {
        return where(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(String column) {
        return new Condition<>(sql, query, column, Where::new);
    }

    @Override
    public Where whereRaw(String condition) {
        return null;
    }
}
