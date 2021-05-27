package eu.software4you.ulib.impl.database.sql.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;
import java.util.function.Function;

import static eu.software4you.utils.ArrayUtils.concat;

final class Condition<R> implements eu.software4you.database.sql.query.Condition<R> {
    protected final Metadata meta;
    protected final String source;
    private final Function<Condition<R>, R> constructor;
    protected boolean not = false;
    protected String condition;

    Condition(Metadata meta, String source, Function<Condition<R>, R> constructor) {
        this.meta = meta;
        this.source = source;
        this.constructor = constructor;
    }

    private R op(@NotNull String operation, @Nullable Object operand) {
        this.condition = String.format("%s %s", operation, operand);
        return constructor.apply(this);
    }

    private R opP(@NotNull String operation) {
        meta.skipParam();
        return op(operation, "?");
    }

    private R opP(@NotNull String operation, @Nullable Object x) {
        meta.opObj(x);
        return op(operation, "?");
    }

    @Override
    public Condition<R> not() {
        this.not = true;
        return this;
    }

    @Override
    public R isEqualToP() {
        return opP("=");
    }

    @Override
    public R isEqualToP(Object what) {
        return opP("=", what);
    }

    @Override
    public R isEqualTo(@NotNull Object what) {
        return op("=", what);
    }

    @Override
    public R isGreaterThan(@NotNull Object than) {
        return op(">", than);
    }

    @Override
    public R isGreaterThanP() {
        return opP(">");
    }

    @Override
    public R isGreaterThanP(Object than) {
        return opP(">", than);
    }

    @Override
    public R isGreaterOrEquals(@NotNull Object than) {
        return op(">=", than);
    }

    @Override
    public R isGreaterOrEqualsP() {
        return opP(">=");
    }

    @Override
    public R isGreaterOrEqualsP(Object than) {
        return opP(">=", than);
    }

    @Override
    public R isLessThan(@NotNull Object than) {
        return op("<", than);
    }

    @Override
    public R isLessThanP() {
        return opP("<");
    }

    @Override
    public R isLessThanP(Object than) {
        return opP("<", than);
    }

    @Override
    public R isLessOrEquals(@NotNull Object than) {
        return op("<=", than);
    }

    @Override
    public R isLessOrEqualsP() {
        return opP("<=");
    }

    @Override
    public R isLessOrEqualsP(Object than) {
        return opP("<=", than);
    }

    @Override
    public R isBetween(@NotNull Object a, @NotNull Object b) {
        return op("BETWEEN", String.format("%s AND %s", a, b));
    }

    @Override
    public R isBetweenP() {
        return op("BETWEEN", "? AND ?");
    }

    @Override
    public R isBetweenP(@NotNull Object a, @NotNull Object b) {
        meta.opObj(a);
        meta.opObj(b);
        return isBetweenP();
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
    public R isLikeP(@NotNull String pattern) {
        meta.op((i, st) -> Metadata.setString(st, i, pattern));
        return op("LIKE", "?");
    }

    @Override
    public R isIn(@NotNull Object val, Object... vals) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        sj.setEmptyValue("");
        for (Object v : concat(val, vals)) {
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

    @Override
    public R isInP(@NotNull Object val, Object... vals) {
        for (Object v : concat(val, vals)) {
            meta.opObj(v);
        }
        return isInP(1 + vals.length);
    }
}
