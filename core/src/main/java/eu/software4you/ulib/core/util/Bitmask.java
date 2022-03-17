package eu.software4you.ulib.core.util;

import lombok.Getter;
import lombok.Setter;

/**
 * A integer based bit-mask.
 */
public class Bitmask {
    @Getter
    @Setter
    private int value;

    /**
     * Constructs a bitmask with an initial value.
     *
     * @param value the initial bitmask value
     */
    public Bitmask(int value) {
        this.value = value;
    }

    /**
     * Default constructor. All bits are clear.
     */
    public Bitmask() {
    }

    /**
     * Checks if certain bits are present.
     *
     * @param bit the bit(s) to check
     * @return {@code true} if the {@code bit} is present, {@code false} otherwise
     */
    public boolean is(int bit) {
        return (value & bit) == bit;
    }

    /**
     * Sets or clears a bit.
     *
     * @param bit the bit to edit
     * @param set {@code true} to set, {@code false} to clear
     * @see #set(int)
     * @see #clear
     */
    public void set(int bit, boolean set) {
        if (set)
            set(bit);
        else
            clear(bit);
    }

    /**
     * Sets the specified bit.
     * <br>
     * The internal bitmask will be OR-ed with the supplied bit.
     *
     * @param bit the bit to set
     */
    public void set(int bit) {
        value |= bit;
    }

    /**
     * Clears the specified bit.
     * <br>
     * The internal bitmask will be AND-ed with the inverted bit.
     *
     * @param bit the bit to clear
     */
    public void clear(int bit) {
        value &= ~bit;
    }
}
