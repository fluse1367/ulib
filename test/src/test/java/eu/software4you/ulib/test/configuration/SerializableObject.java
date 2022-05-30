package eu.software4you.ulib.test.configuration;

import eu.software4you.ulib.core.configuration.serialization.DeSerializationFactory;
import eu.software4you.ulib.core.configuration.serialization.Serializable;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Conversions;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@RequiredArgsConstructor
public class SerializableObject implements Serializable<SerializableObject.Factory> {
    public static class Factory implements DeSerializationFactory<SerializableObject> {
        @Override
        public SerializableObject deserialize(@NotNull Map<String, Object> serialized) {
            return new SerializableObject(
                    Conversions.tryInt(serialized.get("someInt")).orElseThrow(),
                    (String) serialized.get("someString")
            );
        }
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of(
                "someInt", someInt,
                "someString", someString
        );
    }


    private final int someInt;
    private final String someString;

    @Override
    public boolean equals(Object o) {
        return ReflectUtil.autoEquals(this, o);
    }

    @Override
    public int hashCode() {
        return ReflectUtil.autoHash(this);
    }
}
