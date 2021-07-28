package eu.software4you.ulib;

@FunctionalInterface
public interface ImplFactory<R> {
    R fabricate(Object... objects);
}
