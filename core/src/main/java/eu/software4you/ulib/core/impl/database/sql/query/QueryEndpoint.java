package eu.software4you.ulib.core.impl.database.sql.query;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

class QueryEndpoint implements eu.software4you.ulib.core.database.sql.query.QueryEndpoint {
    protected final Metadata meta;

    private boolean limit = false;

    QueryEndpoint(Metadata meta) {
        this.meta = meta;
    }

    @SneakyThrows
    @Override
    public @NotNull ResultSet query(@NotNull Object @NotNull ... parameters) {
        return build(parameters).executeQuery();
    }

    @SneakyThrows
    @Override
    public int update(@NotNull Object @NotNull ... parameters) {
        return build(parameters).executeUpdate();
    }

    @Override
    public @NotNull PreparedStatement build() {
        return meta.applyOps(meta.sql.prepareStatement(buildRawQuery()));
    }

    @SneakyThrows
    @Override
    public @NotNull PreparedStatement build(@NotNull Object @NotNull ... parameters) {
        var st = build();

        var alreadySet = meta.set();

        for (int i = 0, param = 1; i < parameters.length; param++) {
            if (alreadySet.contains(param))
                continue;

            st.setObject(param, parameters[i]);
            i++;
        }
        return st;
    }

    @Override
    public eu.software4you.ulib.core.database.sql.query.@NotNull QueryEndpoint limit(long limit) {
        if (!this.limit && limit >= 0) {
            meta.query.append(String.format(" LIMIT %d", limit));
            this.limit = true;
        }
        return this;
    }

    @Override
    public @NotNull String buildRawQuery() {
        return meta.query.toString();
    }
}
