package eu.software4you.common.validation;

@Deprecated
public class Initiatable {
    private boolean initiated = false;

    protected final void initiate() {
        if (initiated)
            throw new IllegalStateException("Already initiated!");
        initiated = true;
    }
}
