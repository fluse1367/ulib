package eu.software4you.ulib.core.impl.database.sql;

import eu.software4you.ulib.core.database.sql.Column;
import eu.software4you.ulib.core.database.sql.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class ColumnImpl<T> implements Column<T> {
    private final Class<T> t;
    private final String name;
    private final DataType dataType;
    private final boolean notNull;
    private final boolean autoIncrement;
    private final Column.Index index;
    private final long size;
    private final T defaultValue;
    private final T[] acceptable;
}
