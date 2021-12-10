package eu.software4you.ulib.impl.database.sql;

import eu.software4you.ulib.core.api.database.sql.Column;
import eu.software4you.ulib.core.api.database.sql.ColumnBuilder;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SqlDatabase implements eu.software4you.ulib.core.api.database.sql.SqlDatabase {

    private final String url;
    private final Properties info;

    private final Map<String, Table> tables = new LinkedHashMap<>();
    private Connection connection;

    public SqlDatabase(Connection connection) {
        this.connection = connection;
        url = null;
        info = null;
    }

    protected SqlDatabase(String url, Properties info) {
        this.url = url;
        this.info = info;
    }

    private void validateConnection() {
        if (!isConnected())
            throw new IllegalStateException("Database not connected!");
    }

    /* TODO
    @SneakyThrows
    private void loadTables() {
        validateConnection();

        val meta = connection.getMetaData();
        val tableRes = meta.getTables(null, null, null, new String[]{"TABLE"});
        while (tableRes.next()) {
            String tableCatalog = tableRes.getString("TABLE_CAT");
            String tableName = tableRes.getString("TABLE_NAME");
            String tableSchema = tableRes.getString("TABLE_SCHEM");
            ULib.get().info("Table: " + tableName);

            val colRes = meta.getColumns(
                    tableCatalog,
                    tableSchema,
                    tableName,
                    null
            );

            while (colRes.next()) {
                String colName = colRes.getString("COLUMN_NAME");
                String colType = colRes.getString("TYPE_NAME");
                boolean notNull = colRes.getInt("NULLABLE") == ResultSetMetaData.columnNoNulls;
                boolean autoIncrement = colRes.getString("IS_AUTOINCREMENT").equals("YES");
                int size = colRes.getInt("COLUMN_SIZE");
                String def = colRes.getString("COLUMN_DEF");

                StringJoiner sj = new StringJoiner(", ");
                for (int i = 1; i <= colRes.getMetaData().getColumnCount(); i++) {
                    sj.add(colRes.getMetaData().getColumnName(i) + ": " + colRes.getObject(i));
                }
                ULib.get().info(sj.toString());

                ULib.get().info(String.format("%s has column: %s, type: %s, not null: %b, auto inc: %b, size: %d, default: %s",
                        tableName,
                        colName,
                        colType,
                        notNull,
                        autoIncrement,
                        size,
                        def == null ? "null_" : def
                ));

                new ColumnImpl<>(null,
                        colName,
                        DataType.valueOf(colType),
                        notNull,
                        autoIncrement,
                        null,
                        size,
                        def,

                        )
            }

            ULib.get().info("INDEX: ");

            val indexRes = meta.getIndexInfo(
                    tableCatalog,
                    tableSchema,
                    tableName,
                    true,
                    false
            );


            while (indexRes.next()) {

                StringJoiner sj = new StringJoiner(", ");
                for (int i = 1; i <= indexRes.getMetaData().getColumnCount(); i++) {
                    sj.add(indexRes.getMetaData().getColumnName(i) + ": " + indexRes.getObject(i));
                }
                ULib.get().info(sj.toString());
            }

        }
    }
    */

    @SneakyThrows
    @Override
    public boolean isConnected() {
        return connection != null && !connection.isClosed();
    }

    @SneakyThrows
    @Override
    public void connect() throws IllegalStateException {
        if (isConnected())
            throw new IllegalStateException("Database already connected!");
        if (url == null)
            throw new IllegalArgumentException("Invalid database url: null");
        connection = DriverManager.getConnection(url, info);
    }

    @SneakyThrows
    @Override
    public void disconnect() throws IllegalStateException {
        validateConnection();
        connection.close();
    }

    @Override
    public @NotNull Connection getConnection() throws IllegalStateException {
        validateConnection();
        return connection;
    }

    @Override
    public @NotNull Collection<eu.software4you.ulib.core.api.database.sql.Table> getTables() {
        return Collections.unmodifiableCollection(tables.values());
    }

    @Override
    public @Nullable Table getTable(@NotNull String name) {
        return tables.get(name);
    }

    private Table addTable(String name, Column<?>... columns) {
        if (tables.containsKey(name))
            throw new IllegalStateException(String.format("Table %s already added", name));
        var table = createTable(name, Arrays.stream(columns).collect(Collectors.toMap(
                Column::getName, col -> col
        )));
        tables.put(name, table);
        return table;
    }

    protected abstract Table createTable(String name, Map<String, Column<?>> columns);

    protected abstract String autoIncrementKeyword();

    protected abstract boolean applyIndexBeforeAI();

    @Override
    public @NotNull Table addTable(@NotNull String name, @NotNull Column<?> column, Column<?>... columns) {
        List<Column<?>> cols = new ArrayList<>();
        cols.add(column);
        cols.addAll(Arrays.asList(columns));
        return addTable(name, cols.toArray(new Column[0]));
    }

    @Override
    public @NotNull Table addTable(@NotNull String name, @NotNull ColumnBuilder<?> builder, ColumnBuilder<?>... builders) {
        List<Column<?>> columns = new ArrayList<>(builders.length);
        for (ColumnBuilder<?> cb : builders) {
            columns.add(cb.build());
        }
        return addTable(name, builder.build(), columns.toArray(new Column[0]));
    }
}
