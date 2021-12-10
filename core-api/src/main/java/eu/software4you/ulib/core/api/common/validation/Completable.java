package eu.software4you.ulib.core.api.common.validation;

public class Completable extends Initiable {
    private final String message;
    private boolean completed = false;

    public Completable() {
        this("Already completed!");
    }

    public Completable(String message) {
        this.message = message;
    }

    protected final void complete() {
        if (completed)
            throw new UnsupportedOperationException(message);
        completed = true;
    }
}
