package eu.software4you.ulib.impl.database.sql;

import java.util.StringJoiner;
import java.util.function.Function;

final class Condition<R> implements eu.software4you.database.sql.query.Condition<R> {
    protected final SqlDatabase sql; // only for passing to 'Where'
    protected final StringBuilder query; // only for passing to 'Where'
    protected final String source;
    private final Function<Condition<R>, R> constructor;
    protected boolean not = false;
    protected String condition;

    Condition(SqlDatabase sql, StringBuilder query, String source, Function<Condition<R>, R> constructor) {
        this.sql = sql;
        this.query = query;
        this.source = source;
        this.constructor = constructor;
    }

    private R op(String operation, Object operand) {
        this.condition = String.format("%s %s", operation, operand);
        return constructor.apply(this);
    }

    private R opP(String operation) {
        return op(operation, "?");
    }

    @Override
    public Condition<R> not() {
        this.not = true;
        return this;
    }

    @Override
    public R isEqualTo(Object what) {
        return op("=", what);
    }

    @Override
    public R isEqualToP() {
        return opP("=");
    }

    @Override
    public R isGreaterThan(Object than) {
        return op(">", than);
    }

    @Override
    public R isGreaterThanP() {
        return opP(">");
    }

    @Override
    public R isGreaterOrEquals(Object than) {
        return op(">=", than);
    }

    @Override
    public R isGreaterOrEqualsP() {
        return opP(">=");
    }

    @Override
    public R isLessThan(Object than) {
        return op("<", than);
    }

    @Override
    public R isLessThanP() {
        return opP("<");
    }

    @Override
    public R isLessOrEquals(Object than) {
        return op("<=", than);
    }

    @Override
    public R isLessOrEqualsP() {
        return opP("<=");
    }

    @Override
    public R isBetween(Object a, Object b) {
        return op("BETWEEN", String.format("%s AND %s", a, b));
    }

    @Override
    public R isBetweenP() {
        return op("BETWEEN", "? AND ?");
    }

    @Override
    public R isLike(String pattern) {
        return op("LIKE", pattern);
    }

    @Override
    public R isLikeP() {
        return opP("LIKE");
    }

    @Override
    public R isIn(Object val, Object... vals) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        sj.setEmptyValue("");
        for (Object v : vals) {
            sj.add(v.toString());
        }
        return op("IN", sj.toString());
    }

    @Override
    public R isInP(int amount) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        sj.setEmptyValue("");
        for (int i = 0; i < amount; i++) {
            sj.add("?");
        }
        return op("IN", sj.toString());
    }
}
