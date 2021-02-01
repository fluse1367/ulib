package eu.software4you.database.sql;

import eu.software4you.function.ConstructingFunction;
import eu.software4you.ulib.Await;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A builder for {@link Column}s.
 */
public final class ColumnBuilder<T> {
    @Await
    private static ConstructingFunction<Column<?>> constructor;

    private final Class<T> t;
    private final String name;
    private final DataType dataType;
    private final Collection<T> accept = new ArrayList<>();
    private boolean notNull;
    private boolean autoIncrement;
    private Column.Index index;
    private long size;
    private T defaultValue;

    private ColumnBuilder(Class<T> t, String name, DataType dataType) {
        this.t = t;
        this.name = name;
        this.dataType = dataType;
        this.size = this.dataType.getDefaultSize();
    }

    public static <T> ColumnBuilder<T> of(Class<T> t, String name, DataType dataType) {
        return new ColumnBuilder<>(t, name, dataType);
    }

    public static ColumnBuilder<Object> of(String name, DataType dataType) {
        return new ColumnBuilder<>(null, name, dataType);
    }

    /**
     * Prohibits {@code NULL} column values.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> notNull() {
        this.notNull = true;
        return this;
    }

    /**
     * Sets this column as {@code AUTO_INCREMENT}.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> autoIncrement() {
        this.autoIncrement = true;
        return this;
    }

    /**
     * Sets the index of this column.
     *
     * @param index the index attribute
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> index(Column.Index index) {
        this.index = index;
        return this;
    }

    /**
     * Sets this column as {@code PRIMARY} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> primary() {
        this.index = Column.Index.PRIMARY;
        return this;
    }

    /**
     * Sets this column as {@code UNIQUE} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> unique() {
        this.index = Column.Index.UNIQUE;
        return this;
    }

    /**
     * Sets this column as {@code INDEX} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> index() {
        this.index = Column.Index.INDEX;
        return this;
    }

    /**
     * Sets this column as {@code FULLTEXT} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> fulltext() {
        this.index = Column.Index.FULLTEXT;
        return this;
    }

    /**
     * Sets this column as {@code SPATIAL} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> spatial() {
        this.index = Column.Index.SPATIAL;
        return this;
    }

    /**
     * Sets the size of this column's values.
     *
     * @param size the size
     * @return this
     * @throws IllegalArgumentException when the {@code size} exceeds the maximum size
     * @see DataType#getMaximumSize()
     */
    @NotNull
    public ColumnBuilder<T> size(long size) throws IllegalArgumentException {
        if (dataType.getMaximumSize() < size) {
            throw new IllegalArgumentException(String.format("Maximum capacity of %s (%d) exceeded: %d",
                    dataType.name(), dataType.getMaximumSize(), size));
        }
        this.size = size;
        return this;
    }

    /**
     * Sets the default value of this column.
     *
     * @param defaultValue the value
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> def(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Adds a object to the list of accepted values.
     *
     * @param val the value
     * @return this
     */
    @NotNull
    public ColumnBuilder<T> accept(T val) {
        accept.add(val);
        return this;
    }

    /**
     * Adds one or more objects to the list of accepted values.
     *
     * @param val  the value
     * @param vals the other values
     * @return
     */
    @NotNull
    public ColumnBuilder<T> accept(T val, T... vals) {
        accept(val);
        accept.addAll(Arrays.asList(vals));
        return this;
    }

    /**
     * Builds the column.
     *
     * @return the instance
     */
    @NotNull
    public Column<T> build() {
        return (Column<T>) constructor.apply(t, name, dataType, notNull, autoIncrement, index, size, defaultValue, accept.toArray());
    }
}
