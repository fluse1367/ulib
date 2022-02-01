package eu.software4you.ulib.impl.database.sql;

import eu.software4you.database.sql.Column;
import eu.software4you.database.sql.ColumnBuilder;
import eu.software4you.database.sql.DataType;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.inject.ImplConst;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Impl(ColumnBuilder.class)
@AllArgsConstructor(onConstructor_ = @ImplConst, access = AccessLevel.PRIVATE)
@Getter
final class ColumnImpl<T> implements Column<T> {
    private final Class<T> t;
    private final String name;
    private final DataType dataType;
    private final boolean notNull;
    private final boolean autoIncrement;
    private final Index index;
    private final long size;
    private final T defaultValue;
    private final T[] acceptable;
}
