package eu.software4you.ulib.impl.database.sql;

import eu.software4you.common.collection.Pair;
import eu.software4you.database.sql.Column;
import eu.software4you.database.sql.DataType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class Table implements eu.software4you.database.sql.Table {
    private final SqlDatabase sql;
    @Getter
    private final String name;
    private final Map<String, Column<?>> columns;

    @Override
    public @NotNull Column<?>[] getColumns() {
        return columns.values().toArray(new Column[0]);
    }

    @Override
    public @Nullable Column<?> getColumn(String name) {
        return columns.get(name);
    }

    @SneakyThrows
    @Override
    public boolean create() {
        StringJoiner sj = new StringJoiner(", ");
        StringBuilder sb = new StringBuilder();

        for (Column<?> col : columns.values()) {
            sb.append(String.format("`%s` %s", col.getName(), col.getDataType().name()));

            if (col.getDataType() == DataType.ENUM) {
                StringJoiner j = new StringJoiner("', '", "('", "')");
                j.setEmptyValue("");
                for (Object o : col.getAcceptable()) {
                    j.add(o.toString());
                }
                sb.append(j);
            } else if (col.getSize() >= 0) {
                sb.append(String.format("(%d)", col.getSize()));
            }
            if (col.isNotNull()) {
                sb.append(" not null");
            }
            if (col.isAutoIncrement()) {
                sb.append(" auto_increment");
            }
            if (col.getIndex() != null) {
                sb.append(" ").append(col.getIndex().getSql());
            }
            if (col.getDefaultValue() != null) {
                Object def = col.getDefaultValue();
                if (def instanceof Number) {
                    sb.append(" default ").append(def);
                } else {
                    sb.append(String.format(" default '%s'", def));
                }
            }


            sj.add(sb.toString());
            sb.setLength(0);
        }

        String sql = String.format("create table `%s` (%s);", name, sj.toString());

        return this.sql.prepareStatement(sql).executeUpdate() > 0;
    }

    @SneakyThrows
    @Override
    public boolean drop() {
        return sql.prepareStatement(String.format("drop table `%s`;", name)).executeUpdate() > 0;
    }

    @SneakyThrows
    @Override
    public boolean exists() {
        return sql.prepareStatement(String.format("describe `%s`;", name)).executeQuery().next();
    }

    @Override
    public @NotNull Query select(@NotNull String what, String... select) {
        return sel("select", what, select);
    }

    @Override
    public @NotNull Query selectDistinct(@NotNull String what, @NotNull String @NotNull ... select) {
        return sel("select distinct", what, select);
    }

    private Query sel(String operand, String what, String[] whats) {
        return new Query(sql, this, String.format("%s `%s` from", operand,
                String.join("`, `", concat(what, whats))));
    }

    @Override
    public @NotNull SetQuery update() {
        return new SetQuery(sql, this, "update");
    }

    private <T> T[] concat(T a, T[] arr) {
        T[] strs = Arrays.copyOf(arr, arr.length + 1);
        strs[0] = a;
        System.arraycopy(arr, 0, strs, 1, arr.length);
        return strs;
    }

    @SneakyThrows
    @Override
    public boolean insert(Object value, Object... values) {
        values = concat(value, values);

        StringJoiner vals = new StringJoiner("`, `", "`", "`");
        vals.setEmptyValue("");
        for (Object o : values) {
            vals.add(o.toString());
        }
        String query = String.format("insert into `%s` values (%s)", name, vals);
        return sql.prepareStatement(query).executeUpdate() > 0;
    }

    @SafeVarargs
    @SneakyThrows
    @Override
    public final boolean insert(Pair<String, Object> value, Pair<String, Object>... values) {
        StringJoiner cols = new StringJoiner("`, `", "`", "`");
        cols.setEmptyValue("");
        StringJoiner vals = new StringJoiner("`, `", "`", "`");
        vals.setEmptyValue("");

        for (Pair<String, Object> pair : concat(value, values)) {
            cols.add(pair.getFirst());
            vals.add(pair.getSecond().toString());
        }

        String query = String.format("insert into `%s` (%s) values (%s)", name, cols, vals);
        return sql.prepareStatement(query).executeUpdate() > 0;
    }

    @Override
    public QueryStart delete() {
        return new QueryStart(sql, this, "delete from");
    }

    @SneakyThrows
    @Override
    public boolean truncate() {
        return sql.prepareStatement(String.format("truncate table `%s`", name)).executeUpdate() > 0;
    }
}
