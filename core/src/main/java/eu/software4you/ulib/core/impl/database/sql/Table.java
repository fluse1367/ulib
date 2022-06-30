package eu.software4you.ulib.core.impl.database.sql;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.database.sql.DataType;
import eu.software4you.ulib.core.impl.database.sql.query.*;
import eu.software4you.ulib.core.util.Expect;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

import static eu.software4you.ulib.core.util.ArrayUtil.concat;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Table implements eu.software4you.ulib.core.database.sql.Table {
    protected final SqlDatabase sql;
    @Getter
    protected final String name;
    private final Map<String, Column<?>> columns;

    @Override
    public @NotNull Column<?>[] getColumns() {
        return columns.values().toArray(new Column[0]);
    }

    @Override
    @NotNull
    public Optional<Column<?>> getColumn(@NotNull String name) {
        return Optional.ofNullable(columns.get(name));
    }

    @Override
    public @NotNull Expect<Void, SQLException> create() {
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
            if (sql.applyIndexBeforeAI()) {
                col.getIndex().ifPresent(in -> sb.append(" ").append(in.getSql()));
                if (col.isAutoIncrement()) {
                    sb.append(" ").append(sql.autoIncrementKeyword());
                }
            } else {
                if (col.isAutoIncrement()) {
                    sb.append(" ").append(sql.autoIncrementKeyword());
                }
                col.getIndex().ifPresent(in -> sb.append(" ").append(in.getSql()));
            }
            if (col.getDefaultValue() != null) {
                Object def = col.getDefaultValue();
                sb.append(" default ");
                if (def instanceof Number || def instanceof Boolean) {
                    sb.append(def);
                } else {
                    sb.append(String.format("'%s'", def));
                }
            }


            sj.add(sb.toString());
            sb.setLength(0);
        }

        String sql = String.format("create table `%s` (%s);", name, sj);

        return Expect.compute(() -> {
            try (var st = this.sql.prepareStatement(sql)) {
                st.executeUpdate();
            }
        });
    }

    @SneakyThrows
    @Override
    public boolean drop() {
        return sql.prepareStatement(String.format("drop table `%s`;", name)).executeUpdate() > 0;
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

    @SneakyThrows
    @Override
    public boolean insert(@NotNull Object v, Object... vs) {
        var values = concat(v, vs);

        StringJoiner vals = new StringJoiner(", ", "(", ")");
        vals.setEmptyValue("()");
        for (Object o : values) {
            vals.add("?");
        }
        String query = String.format("insert into `%s` values %s", name, vals);

        var st = sql.prepareStatement(query);
        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i]);
        }
        return st.executeUpdate() > 0;
    }

    @SafeVarargs
    @SneakyThrows
    @Override
    public final boolean insert(@NotNull Pair<String, Object> v, Pair<String, Object>... vs) {
        var values = concat(v, vs);

        StringJoiner columnsStr = new StringJoiner(", ", "(", ")");
        columnsStr.setEmptyValue("()");
        StringJoiner valuesStr = new StringJoiner(", ", "(", ")");
        valuesStr.setEmptyValue("()");

        for (Pair<String, Object> pair : values) {
            columnsStr.add(pair.getFirst());
            valuesStr.add("?");
        }

        String query = String.format("insert into `%s` %s values %s", name, columnsStr, valuesStr);
        var st = sql.prepareStatement(query);
        for (int i = 0; i < values.length; i++) {
            st.setObject(i + 1, values[i].getSecond());
        }
        return st.executeUpdate() > 0;
    }

    @Override
    @NotNull
    public QueryStart delete() {
        return new QueryStart(sql, this, "delete from");
    }

    @SneakyThrows
    @Override
    public boolean truncate() {
        return sql.prepareStatement(String.format("truncate table `%s`", name)).executeUpdate() > 0;
    }
}
