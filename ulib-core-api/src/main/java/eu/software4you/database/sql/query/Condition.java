package eu.software4you.database.sql.query;

public interface Condition<D> {
    Condition<D> not();

    D isEqualTo(Object what);

    D isGreaterThan(Object than);

    D isGreaterOrEquals(Object than);

    D isLessThan(Object than);

    D isLessOrEquals(Object than);

    D isBetween(Object a, Object b);

    D isLike(String pattern);

    D isIn(Object val, Object... vals);
}
