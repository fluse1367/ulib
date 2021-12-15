package eu.software4you.ulib.core.api.database.sql;

import eu.software4you.ulib.core.api.common.Nameable;
import eu.software4you.ulib.core.api.common.Sizable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a column in a {@link Table}.
 *
 * @param <T> the data type
 */
public interface Column<T> extends Nameable, Sizable {
    /**
     * Returns the data type this column is using.
     *
     * @return the data type
     * @see T
     */
    @NotNull DataType getDataType();

    /**
     * Returns the default value of this column
     *
     * @return the value
     */
    @Nullable T getDefaultValue();

    /**
     * Returns the valid values that can be inserted into this column,
     * or {@code null} if everything is acceptable
     *
     * @return the valid values
     */
    @NotNull T[] getAcceptable();

    @Override
    @NotNull String getName();

    /**
     * Checks if this column can have {@code NULL} values.
     *
     * @return the value
     */
    boolean isNotNull();

    /**
     * Checks if this column is marked as {@code AUTO_INCREMENT}.
     *
     * @return the value
     */
    boolean isAutoIncrement();

    /**
     * Returns the index value of the column.
     *
     * @return the value, or {@code null} if not set
     */
    @Nullable
    Index getIndex();

    @RequiredArgsConstructor
    enum Index {
        PRIMARY("primary key"),
        UNIQUE("unique"),
        INDEX("index"),
        FULLTEXT("fulltext"),
        SPATIAL("spatial"),
        ;
        @Getter
        private final String sql;
    }
}