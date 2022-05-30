package eu.software4you.ulib.core.impl.database.sql;

import eu.software4you.ulib.core.database.sql.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ColBuilder<T> implements ColumnBuilder<T> {
    private final Class<T> t;
    private final String name;
    private final DataType dataType;
    private final Collection<T> accept = new ArrayList<>();
    private boolean notNull;
    private boolean autoIncrement;
    private Column.Index index;
    private long size;
    private T defaultValue;

    public ColBuilder(Class<T> t, String name, DataType dataType) {
        this.t = t;
        this.name = name;
        this.dataType = dataType;
        this.size = this.dataType.getDefaultSize();
    }

    @NotNull
    public ColumnBuilder<T> notNull() {
        this.notNull = true;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> autoIncrement() {
        this.autoIncrement = true;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> index(Column.Index index) {
        this.index = index;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> primary() {
        this.index = Column.Index.PRIMARY;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> unique() {
        this.index = Column.Index.UNIQUE;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> index() {
        this.index = Column.Index.INDEX;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> fulltext() {
        this.index = Column.Index.FULLTEXT;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> spatial() {
        this.index = Column.Index.SPATIAL;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> size(long size) throws IllegalArgumentException {
        if (dataType.getMaximumSize() < size) {
            throw new IllegalArgumentException(String.format("Maximum capacity of %s (%d) exceeded: %d",
                    dataType.name(), dataType.getMaximumSize(), size));
        }
        this.size = size;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> def(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @NotNull
    public ColumnBuilder<T> accept(@NotNull T val) {
        accept.add(val);
        return this;
    }

    @SafeVarargs
    @NotNull
    public final ColumnBuilder<T> accept(@NotNull T val, T @NotNull ... vals) {
        accept(val);
        accept.addAll(Arrays.asList(vals));
        return this;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public Column<T> build() {
        return new ColumnImpl<T>(t, name, dataType, notNull, autoIncrement, index, size, defaultValue, (T[]) accept.toArray());
    }
}
