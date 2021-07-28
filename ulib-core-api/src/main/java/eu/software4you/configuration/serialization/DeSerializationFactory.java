package eu.software4you.configuration.serialization;

import java.util.Map;

/**
 * Interface for object deserialization with the {@link eu.software4you.configuration.Configurations Configuration API}.
 * <p>
 * Any class implementing this interface is able to deserialize the declared objects.
 * <p>
 * This class <b>must</b> have either a public default constructor, or a public static method {@code getInstance()} which returns a factory singleton instance.
 * <p>
 * Please see {@link Serializable} for more <b>important</b> information and a code example.
 *
 * @param <T> the class that this factory fabricates
 * @see Serializable
 */
public interface DeSerializationFactory<T extends Serializable<?>> {
    /**
     * Creates an object out of the serialized data.
     *
     * @param serialized the serialized data
     * @return the newly created object
     */
    T deserialize(Map<String, Object> serialized);
}
