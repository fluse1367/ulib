package eu.software4you.ulib.impl.database.sql.mysql;

import eu.software4you.ulib.core.api.database.sql.Column;
import eu.software4you.ulib.impl.database.sql.SqlDatabase;
import eu.software4you.ulib.impl.database.sql.Table;
import lombok.SneakyThrows;

import java.util.Map;

final class MySQLTable extends Table {

    MySQLTable(SqlDatabase sql, String name, Map<String, Column<?>> columns) {
        super(sql, name, columns);
    }

    @SneakyThrows
    @Override
    public boolean exists() {
        var st = sql.prepareStatement("select count(*) from `information_schema`.`tables` where `table_schema` = database() AND `table_name` = ?");
        st.setString(1, name);
        var res = st.executeQuery();
        if (res.next()) {
            return res.getInt("count(*)") > 0;
        }
        return false;
    }
}
