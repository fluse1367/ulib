package eu.software4you.database.sql.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface QueryEndpoint {
    ResultSet query(Object... parameters);

    void update(Object... parameters);

    PreparedStatement build();

    QueryEndpoint limit(long number);
}
