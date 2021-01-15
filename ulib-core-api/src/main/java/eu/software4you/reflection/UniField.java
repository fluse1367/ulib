package eu.software4you.reflection;

import java.lang.reflect.Field;

public class UniField {
    private final Field field;

    public UniField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    /**
     * @param classInstance object from which the represented field's value is to be extracted
     * @return the value of the represented field in object {@code obj}; primitive values are wrapped in an appropriate object before being returned
     */
    public Object get(Object classInstance) {
        try {
            return field.get(classInstance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param classInstance the object whose field should be modified
     * @param value         the new value for the field of {@code obj}
     * @return true on success, false on failure
     */
    public boolean set(Object classInstance, Object value) {
        try {
            field.set(classInstance, value);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return the actual {@link Field} object
     */
    public Field raw() {
        return field;
    }
}
