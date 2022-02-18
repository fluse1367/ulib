package eu.software4you.ulib.core.api.common.validation;

public class Initiable {
    private final String message;
    private boolean initiated = false;

    public Initiable() {
        this("Already initiated!");
    }

    public Initiable(String message) {
        this.message = message;
    }

    public void initiate() {
        if (initiated)
            throw new UnsupportedOperationException(message);
        initiated = true;
    }
}
