package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

public interface Where extends QueryEndpoint {
    Condition<Where> and(Column<?> column);

    Condition<Where> and(String column);

    Where andRaw(String condition);

    Condition<Where> or(Column<?> column);

    Condition<Where> or(String column);

    Where orRaw(String condition);
}
