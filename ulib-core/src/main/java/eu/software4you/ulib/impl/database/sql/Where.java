package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;

final class Where extends QueryEndpoint implements eu.software4you.database.sql.query.Where {

    Where(Condition<eu.software4you.database.sql.query.Where> condition) {
        this(condition, "where");
    }

    Where(Condition<eu.software4you.database.sql.query.Where> condition, String operand) {
        super(condition.sql, condition.query);
        query.append(String.format("%s %s`%s` %s",
                operand, condition.not ? "not " : "", condition.source, condition.condition));
    }

    Where(SqlDatabase sql, StringBuilder query, String condition) {
        super(sql, query);
        append(" where", condition);
    }

    private void append(String operand, String condition) {
        query.append(operand).append(" ").append(condition);
    }


    @Override
    public Condition<eu.software4you.database.sql.query.Where> and(Column<?> column) {
        return and(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> and(String column) {
        return new Condition<>(sql, query, column, c -> new Where(c, " and"));
    }

    @Override
    public Where andRaw(String condition) {
        append(" and", condition);
        return this;
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> or(Column<?> column) {
        return or(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> or(String column) {
        return new Condition<>(sql, query, column, c -> new Where(c, " or"));
    }

    @Override
    public Where orRaw(String condition) {
        append(" or", condition);
        return this;
    }
}
