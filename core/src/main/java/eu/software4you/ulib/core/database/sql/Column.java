package eu.software4you.ulib.core.database.sql;

import eu.software4you.ulib.core.common.Keyable;
import eu.software4you.ulib.core.common.Sizable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a column in a {@link Table}.
 *
 * @param <T> the data type
 */
public interface Column<T> extends Keyable<String>, Sizable {
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

    @NotNull String getName();

    @Override
    @NotNull
    default String getKey() {
        return getName();
    }

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
    @NotNull
    Optional<Index> getIndex();

    @RequiredArgsConstructor
    enum Index {
        PRIMARY("primary key"),
        UNIQUE("unique"),
        INDEX("index"),
        FULLTEXT("fulltext"),
        SPATIAL("spatial"),
        ;
        @Getter
        @NotNull
        private final String sql;
    }
}
