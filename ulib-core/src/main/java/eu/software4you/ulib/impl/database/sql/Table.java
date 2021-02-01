package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;
import eu.software4you.database.sql.DataType;
import eu.software4you.database.sql.SqlDatabase;
import eu.software4you.database.sql.query.Query;
import eu.software4you.database.sql.query.SetQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.StringJoiner;

@RequiredArgsConstructor
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
    public void create() {
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
                sb.append(j.toString());
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

        this.sql.prepareStatement(sql).executeUpdate();
    }

    @SneakyThrows
    @Override
    public void drop() {
        sql.prepareStatement(String.format("drop table `%s`;", name)).executeUpdate();
    }

    @SneakyThrows
    @Override
    public boolean exists() {
        return sql.prepareStatement(String.format("describe `%s`;", name)).executeQuery().next();
    }

    @Override
    public Query select(String what, String... select) {
        throw new NotImplementedException();
    }

    @Override
    public Query selectDistinct(String what, String... select) {
        throw new NotImplementedException();
    }

    @Override
    public SetQuery update() {
        throw new NotImplementedException();
    }
}
