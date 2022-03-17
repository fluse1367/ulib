package eu.software4you.ulib.core.configuration.serialization;

import java.util.Map;


/**
 * Interface for object serialization with the Configuration API.
 * <p>
 * Any class implementing this interface is able to get serialized.
 * <p>
 * Additionally a {@link DeSerializationFactory factory} class must be declared as the type parameter {@code T}.
 * This factory class <b>must</b> implement the {@link DeSerializationFactory} interface and supply the declaring class as type parameter.
 * Please see {@link DeSerializationFactory} for more <b>important</b> information.
 * <br>
 * Here is an example of a class that works with full serialization and deserialization:
 * <pre>{@code
 * class Data implements Serializable<Data.Factory> {
 *      public static class Factory implements DeSerializationFactory<Data> {
 *          private static final Factory instance = new Factory();
 *
 *          public static Factory getInstance() {
 *              return instance;
 *          }
 *
 *          private Factory() {}
 *
 *          public Data deserialize(Map<String, Object> serialized) {
 *              // ...
 *          }
 *      }
 *
 *      public Map<String, Object> serialize() {
 *          //...
 *      }
 * }
 * }</pre>
 *
 * @param <T> the factory class
 * @see DeSerializationFactory
 */
public interface Serializable<T extends DeSerializationFactory<?>> {
    /**
     * Creates a map, containing all data necessary for deserialization/re-instantiation of this object.
     *
     * @return map with serialized data
     */
    Map<String, Object> serialize();
}
