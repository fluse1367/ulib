package eu.software4you.ulib.impl.database.sql;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

class Metadata {
    protected final SqlDatabase sql;
    protected final StringBuilder query;
    private final Map<Integer, BiConsumer<Integer, PreparedStatement>> operations;
    private int offset = 0;

    Metadata(SqlDatabase sql, StringBuilder query) {
        this.sql = sql;
        this.query = query;
        this.operations = new HashMap<>();
    }

    // helper methods to utilize @SneakyThrows
    @SneakyThrows
    static void setObject(PreparedStatement st, int i, Object x) {
        st.setObject(i, x);
    }

    @SneakyThrows
    static void setString(PreparedStatement st, int i, String x) {
        st.setString(i, x);
    }

    protected void op(BiConsumer<Integer, PreparedStatement> op) {
        operations.put(offset++, op);
    }

    protected void opObj(Object obj) {
        op((i, st) -> setObject(st, i, obj));
    }

    protected Set<Integer> applyOps(PreparedStatement st) {

        Set<Integer> set = new HashSet<>();
        for (Map.Entry<Integer, BiConsumer<Integer, PreparedStatement>> en : operations.entrySet()) {
            val off = en.getKey();
            val con = en.getValue();

            int param = 1 + off;
            con.accept(param, st);
            set.add(param);
        }

        return set;
    }

    protected void skipParam() {
        offset++;
    }
}
