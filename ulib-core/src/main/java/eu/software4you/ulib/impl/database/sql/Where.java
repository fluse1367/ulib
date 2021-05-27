package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;
import org.jetbrains.annotations.NotNull;

final class Where extends QueryEndpoint implements eu.software4you.database.sql.query.Where {

    Where(Condition<eu.software4you.database.sql.query.Where> condition) {
        this(condition, "where");
    }

    Where(Condition<eu.software4you.database.sql.query.Where> condition, String operand) {
        super(condition.meta);
        meta.query.append(String.format(" %s %s`%s` %s",
                operand, condition.not ? "not " : "", condition.source, condition.condition));
    }

    Where(Metadata meta, String condition) {
        super(meta);
        append("where", condition);
    }

    private void append(String operand, String condition) {
        meta.query.append(String.format(" %s %s", operand, condition));
    }


    @Override
    public Condition<eu.software4you.database.sql.query.Where> and(@NotNull Column<?> column) {
        return and(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> and(@NotNull String column) {
        return new Condition<>(meta, column, c -> new Where(c, " and"));
    }

    @Override
    public Where andRaw(@NotNull String condition) {
        append("and", condition);
        return this;
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> or(@NotNull Column<?> column) {
        return or(column.getName());
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> or(@NotNull String column) {
        return new Condition<>(meta, column, c -> new Where(c, " or"));
    }

    @Override
    public Where orRaw(@NotNull String condition) {
        append("or", condition);
        return this;
    }
}
