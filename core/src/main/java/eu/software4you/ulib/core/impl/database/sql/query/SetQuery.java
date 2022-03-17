package eu.software4you.ulib.core.impl.database.sql.query;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.core.impl.database.sql.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.*;

public final class SetQuery extends Query implements eu.software4you.ulib.core.database.sql.query.SetQuery {
    private final List<Pair<String, Object>> sets = new ArrayList<>();

    public SetQuery(SqlDatabase sql, Table table, String what) {
        super(sql, table, what);
    }

    @Override
    public SetQuery setP(@NotNull Column<?> column) {
        return setP(column.getName());
    }

    @Override
    public SetQuery setP(@NotNull String column) {
        meta.skipParam();
        sets.add(new Pair<>(column, "?"));
        return this;
    }

    @Override
    public SetQuery setP(@NotNull Column<?> column, Object to) {
        return setP(column.getName(), to);
    }

    @Override
    public SetQuery setP(@NotNull String column, Object to) {
        meta.opObj(to);
        sets.add(new Pair<>(column, "?"));
        return this;
    }

    @Override
    public SetQuery set(@NotNull Column<?> column, @NotNull Object to) {
        return set(column.getName(), to);
    }

    @Override
    public SetQuery set(@NotNull String column, @NotNull Object to) {
        sets.add(new Pair<>(column, to));
        return this;
    }

    private void append() {
        StringJoiner sj = new StringJoiner(", ", " set ", "");
        sets.forEach(pair -> sj.add(String.format("%s = %s", pair.getFirst(), pair.getSecond())));
        meta.query.append(sj);
    }

    @Override
    public Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull Column<?> column) {
        append();
        return super.where(column);
    }

    @Override
    public Condition<eu.software4you.ulib.core.database.sql.query.Where> where(@NotNull String column) {
        append();
        return super.where(column);
    }

    @Override
    public Where whereRaw(@NotNull String condition) {
        append();
        return super.whereRaw(condition);
    }

    @Override
    public PreparedStatement build() {
        append();
        return super.build();
    }
}
