package eu.software4you.function;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void apply(A a, B b, C c);
}
