package eu.software4you.ulib.core.database.sql;

import eu.software4you.ulib.core.impl.database.sql.ColBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A builder for {@link Column}s.
 */
public interface ColumnBuilder<T> {
    static <T> ColumnBuilder<T> of(Class<T> t, String name, DataType dataType) {
        return new ColBuilder<>(t, name, dataType);
    }

    static ColumnBuilder<Object> of(String name, DataType dataType) {
        return of(null, name, dataType);
    }

    static ColumnBuilder<?> of(DataType dataType, String name) {
        return of(dataType.getClazz(), name, dataType);
    }

    /**
     * Prohibits {@code NULL} column values.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> notNull();

    /**
     * Sets this column as {@code AUTO_INCREMENT}.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> autoIncrement();

    /**
     * Sets the index of this column.
     *
     * @param index the index attribute
     * @return this
     */
    @NotNull ColumnBuilder<T> index(Column.Index index);

    /**
     * Sets this column as {@code PRIMARY} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> primary();

    /**
     * Sets this column as {@code UNIQUE} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> unique();

    /**
     * Sets this column as {@code INDEX} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> index();

    /**
     * Sets this column as {@code FULLTEXT} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> fulltext();

    /**
     * Sets this column as {@code SPATIAL} in a table.
     * The index attribute will be overwritten.
     *
     * @return this
     */
    @NotNull ColumnBuilder<T> spatial();

    /**
     * Sets the size of this column's values.
     *
     * @param size the size
     * @return this
     * @throws IllegalArgumentException when the {@code size} exceeds the maximum size
     * @see DataType#getMaximumSize()
     */
    @NotNull ColumnBuilder<T> size(long size);

    /**
     * Sets the default value of this column.
     *
     * @param defaultValue the value
     * @return this
     */
    @NotNull ColumnBuilder<T> def(T defaultValue);

    /**
     * Adds a object to the list of accepted values.
     *
     * @param val the value
     * @return this
     */
    @NotNull ColumnBuilder<T> accept(T val);

    /**
     * Adds one or more objects to the list of accepted values.
     *
     * @param val  the value
     * @param vals the other values
     * @return this
     */
    @NotNull ColumnBuilder<T> accept(T val, T... vals);

    /**
     * Builds the column.
     *
     * @return the instance
     */
    @NotNull Column<T> build();
}
