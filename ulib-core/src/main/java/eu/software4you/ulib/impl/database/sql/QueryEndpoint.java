package eu.software4you.ulib.impl.database.sql;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

class QueryEndpoint implements eu.software4you.database.sql.query.QueryEndpoint {
    protected final Metadata meta;

    private boolean limit = false;

    QueryEndpoint(Metadata meta) {
        this.meta = meta;
    }

    @SneakyThrows
    @Override
    public ResultSet query(Object... parameters) {
        return build(parameters).executeQuery();
    }

    @SneakyThrows
    @Override
    public int update(Object... parameters) {
        return build(parameters).executeUpdate();
    }

    @Override
    public PreparedStatement build() {
        return meta.sql.prepareStatement(buildRawQuery());
    }

    @SneakyThrows
    @Override
    public PreparedStatement build(Object... parameters) {
        val st = build();

        val alreadySet = meta.applyOps(st);

        for (int i = 0, param = 1; i < parameters.length; param++) {
            if (alreadySet.contains(param))
                continue;

            st.setObject(param, parameters[i]);
            i++;
        }
        return st;
    }

    @Override
    public QueryEndpoint limit(long limit) {
        if (!this.limit && limit >= 0) {
            meta.query.append(String.format(" LIMIT %d", limit));
            this.limit = true;
        }
        return this;
    }

    @Override
    public String buildRawQuery() {
        return meta.query.toString();
    }
}
