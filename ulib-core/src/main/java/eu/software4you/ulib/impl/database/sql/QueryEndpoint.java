package eu.software4you.ulib.impl.database.sql;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

class QueryEndpoint implements eu.software4you.database.sql.query.QueryEndpoint {
    protected final SqlDatabase sql;
    protected final StringBuilder query;

    private boolean limit = false;

    QueryEndpoint(SqlDatabase sql, StringBuilder query) {
        this.sql = sql;
        this.query = query;
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
        return sql.prepareStatement(query.toString());
    }

    @SneakyThrows
    @Override
    public PreparedStatement build(Object... parameters) {
        val st = build();
        for (int i = 0; i < parameters.length; i++) {
            st.setObject(i + 1, parameters[i]);
        }
        return st;
    }

    @Override
    public QueryEndpoint limit(long limit) {
        if (!this.limit && limit >= 0) {
            query.append(String.format(" LIMIT %d", limit));
            this.limit = true;
        }
        return this;
    }
}
