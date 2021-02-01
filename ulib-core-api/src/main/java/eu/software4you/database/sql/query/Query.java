package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

public interface Query extends QueryEndpoint {
    Condition<Where> where(Column<?> column);

    Condition<Where> where(String column);

    Where whereRaw(String condition);
}
