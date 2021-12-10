package eu.software4you.ulib.core.api.common.collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// Duplicated Triple and modified, see README file for copyright information

/**
 * This class can capture 4 references of 4 types and set or
 * clear the data using setFirst() / getFirst() and setSecond() / getSecond().
 * It can be used to return multiple objects of a method, or to
 * easily capture multiple objects without creating their own class.
 *
 * @param <F> the first type, which you want to defined
 * @param <S> the second type which you want to defined
 * @param <T> the third type which you want to defined
 * @param <Q> the fourth type which you want to defined
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Quadruple<F, S, T, Q> {

    /**
     * The reference of the first value and the type of F
     *
     * @see F
     */
    protected F first;

    /**
     * The reference of the second value and the type of S
     *
     * @see S
     */
    protected S second;

    /**
     * The reference of the third value and the type of T
     *
     * @see T
     */
    protected T third;

    /**
     * The reference of the fourth value and the type of Q
     *
     * @see Q
     */
    protected Q fourth;

    public Quadruple(F first, S second, T third, Q fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public Quadruple() {
    }
}