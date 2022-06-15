package eu.software4you.ulib.core.impl.database.sql;

import eu.software4you.ulib.core.database.sql.*;
import eu.software4you.ulib.core.dependencies.Dependencies;
import eu.software4you.ulib.core.dependencies.Repository;
import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Expect;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public abstract class SqlDatabase implements eu.software4you.ulib.core.database.sql.SqlDatabase {

    private final String url;
    private final Properties info;

    private final Map<String, Table> tables = new LinkedHashMap<>();
    private Connection connection;

    public SqlDatabase(Connection connection) {
        this.connection = connection;
        initExistingTables();
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
        try {
            connection = DriverManager.getConnection(url, info);
        } catch (SQLException e) {
            if (!e.getSQLState().equals("08001")) {
                throw e;
            }
            // driver cloud not be loaded
            loadDriver();

            // try again
            connection = DriverManager.getConnection(url, info);
        }
        initExistingTables();
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
    public @NotNull Collection<eu.software4you.ulib.core.database.sql.Table> getTables() {
        return Collections.unmodifiableCollection(tables.values());
    }

    @Override
    @SneakyThrows
    public int fetchLastAutoincrementInsertionId() {
        try (var st = prepareStatement("select %s() limit 1;".formatted(lastInsertId()));
             var res = st.executeQuery()) {
            return res.getInt(1);
        }
    }

    @Override
    @NotNull
    public Optional<eu.software4you.ulib.core.database.sql.Table> getTable(@NotNull String name) {
        // attempt fetching tables if `name` does not occur in the map
        if (!tables.containsKey(name))
            initExistingTables();
        return Optional.ofNullable(tables.get(name));
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

    @SneakyThrows
    private void initExistingTables() {
        if (!isConnected())
            return;

        var meta = connection.getMetaData();
        var resultTables = meta.getTables(null, null, null, new String[]{"TABLES", "TABLE"});


        while (resultTables.next()) {
            String tableCatalog = resultTables.getString("TABLE_CAT");
            String tableName = resultTables.getString("TABLE_NAME");
            String tableSchema = resultTables.getString("TABLE_SCHEM");

            var cols = fetchColumns(meta, tableCatalog, tableSchema, tableName);

            tables.putIfAbsent(tableName, createTable(tableName, cols));
        }
    }

    // helper method
    @SneakyThrows
    private Map<String, Column<?>> fetchColumns(DatabaseMetaData meta, String tableCatalog, String tableSchema, String tableName) {
        var resultColumns = meta.getColumns(
                tableCatalog,
                tableSchema,
                tableName,
                null
        );

        Map<String, Column<?>> cols = new HashMap<>();

        while (resultColumns.next()) {
            String colName = resultColumns.getString("COLUMN_NAME");
            String colType = resultColumns.getString("TYPE_NAME");
            boolean notNull = resultColumns.getInt("NULLABLE") == ResultSetMetaData.columnNoNulls;
            boolean autoIncrement = resultColumns.getString("IS_AUTOINCREMENT").equals("YES");
            int size = resultColumns.getInt("COLUMN_SIZE");
            String def = resultColumns.getString("COLUMN_DEF");

            var column = new ColumnImpl<>(null,
                    colName,
                    DataType.valueOf(colType),
                    notNull,
                    autoIncrement,
                    null,
                    size,
                    def,
                    new String[0]);
            cols.put(colName, column);
        }

        return cols;
    }

    protected abstract Table createTable(String name, Map<String, Column<?>> columns);

    protected abstract String autoIncrementKeyword();

    protected abstract boolean applyIndexBeforeAI();

    protected abstract String lastInsertId();

    protected void loadDriver() throws IOException {
        // download all files
        var files = Dependencies.require(driverCoordinates(), Repository.mavenCentral())
                .orElseThrow()
                .map(Path::toFile)
                .map(f -> Expect.compute(JarFile::new, f).orElseThrow())
                .toList();

        // append them all to the system class loader
        files.forEach(Internal.getInstrumentation()::appendToSystemClassLoaderSearch);

        // load the driver(s)
        for (JarFile file : files) {
            try (file) {

                // get the service
                var serviceDeclaration = file.getJarEntry("META-INF/services/java.sql.Driver");
                if (serviceDeclaration == null)
                    continue; // no such service

                // read the service class name
                String service;
                try (var in = file.getInputStream(serviceDeclaration)) {
                    service = IOUtil.toString(in).orElseThrow()
                            .lines().findFirst().orElseThrow();
                }

                // finally, load it!
                try {
                    Class.forName(service, true, ClassLoader.getSystemClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new InternalError(e);
                }
            }
        }

    }

    protected String driverCoordinates() {
        throw new UnsupportedOperationException();
    }

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
