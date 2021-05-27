package eu.software4you.ulib.impl.database.sql;

import eu.software4you.common.collection.Pair;
import eu.software4you.database.sql.Column;
import eu.software4you.database.sql.DataType;
import eu.software4you.database.sql.MySQLDatabase;
import lombok.*;
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
    public @Nullable Column<?> getColumn(@NotNull String name) {
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

        String sql = String.format("create table `%s` (%s);", name, sj);

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
        String query = sql instanceof MySQLDatabase ?
                /*mysql*/ "select count(*) from `information_schema`.`tables` where `table_schema` = database() AND `table_name` = '%s'"
                /*sqlite*/ : "select count(*) from `sqlite_master` where `type` = 'table' and `name` = '%s'";
        val res = sql.prepareStatement(String.format(query, name)).executeQuery();
        if (res.next()) {
            return res.getInt("count(*)") > 0;
        }
        return false;
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
        return new Query(sql, this, String.format("%s %s from", operand,
                String.join(", ", concat(what, whats))));
    }

    @Override
    public @NotNull SetQuery update() {
        return new SetQuery(sql, this, "update");
    }

    static <T> T[] concat(T a, T[] arr) {
        T[] strs = Arrays.copyOf(arr, arr.length + 1);
        strs[0] = a;
        System.arraycopy(arr, 0, strs, 1, arr.length);
        return strs;
    }

    @SneakyThrows
    @Override
    public boolean insert(@NotNull Object v, Object... vs) {
        val values = concat(v, vs);

        StringJoiner vals = new StringJoiner(", ", "(", ")");
        vals.setEmptyValue("()");
        for (Object o : values) {
            vals.add("?");
        }
        String query = String.format("insert into `%s` values %s", name, vals);

        val st = sql.prepareStatement(query);
        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i]);
        }
        return st.executeUpdate() > 0;
    }

    @SafeVarargs
    @SneakyThrows
    @Override
    public final boolean insert(@NotNull Pair<String, Object> v, Pair<String, Object>... vs) {
        val values = concat(v, vs);

        StringJoiner columnsStr = new StringJoiner(", ", "(", ")");
        columnsStr.setEmptyValue("()");
        StringJoiner valuesStr = new StringJoiner(", ", "(", ")");
        valuesStr.setEmptyValue("()");

        for (Pair<String, Object> pair : values) {
            columnsStr.add(pair.getFirst());
            valuesStr.add("?");
        }

        String query = String.format("insert into `%s` %s values %s", name, columnsStr, valuesStr);
        val st = sql.prepareStatement(query);
        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i].getSecond());
        }
        return st.executeUpdate() > 0;
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
