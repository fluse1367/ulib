package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;

class Query extends QueryEndpoint implements eu.software4you.database.sql.query.Query {

    Query(SqlDatabase sql, Table table, String operand) {
        super(sql, new StringBuilder(String.format("%s `%s`", operand, table.getName())));
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
        return new Where(sql, query, condition);
    }
}
