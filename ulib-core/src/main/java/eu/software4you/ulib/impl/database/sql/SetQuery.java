package eu.software4you.ulib.impl.database.sql;

import eu.software4you.common.collection.Pair;
import eu.software4you.database.sql.Column;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

final class SetQuery extends Query implements eu.software4you.database.sql.query.SetQuery {
    private final List<Pair<String, Object>> sets = new ArrayList<>();

    SetQuery(SqlDatabase sql, Table table, String what) {
        super(sql, table, what);
    }

    @Override
    public SetQuery set(Column<?> column, Object to) {
        return set(column.getName(), to);
    }

    @Override
    public SetQuery setP(Column<?> column) {
        return setP(column.getName());
    }

    @Override
    public SetQuery set(String column, Object to) {
        sets.add(new Pair<>(column, to));
        return this;
    }

    @Override
    public SetQuery setP(String column) {
        sets.add(new Pair<>(column, "?"));
        return this;
    }

    private void append() {
        StringJoiner sj = new StringJoiner(", ", "set ", "");
        sets.forEach(pair -> sj.add(String.format("%s = %s", pair.getFirst(), pair.getSecond())));
        query.append(sj);
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(Column<?> column) {
        append();
        return super.where(column);
    }

    @Override
    public Condition<eu.software4you.database.sql.query.Where> where(String column) {
        append();
        return super.where(column);
    }

    @Override
    public Where whereRaw(String condition) {
        append();
        return super.whereRaw(condition);
    }

    @Override
    public PreparedStatement build() {
        append();
        return super.build();
    }
}
