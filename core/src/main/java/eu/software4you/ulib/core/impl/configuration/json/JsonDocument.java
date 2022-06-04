package eu.software4you.ulib.core.impl.configuration.json;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.impl.configuration.ConfigurationBase;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;

public class JsonDocument extends ConfigurationBase<JsonDocument> implements JsonConfiguration {

    private final JsonSerializer serializer;

    // construct as empty root
    JsonDocument(JsonSerializer serializer) {
        super();
        this.serializer = serializer;
    }

    // construct as sub
    JsonDocument(JsonSerializer serializer, JsonDocument root, JsonDocument parent, String key) {
        super(root, parent, key);
        this.serializer = serializer;
    }

    @Override
    protected JsonDocument constructSub(String key) {
        return new JsonDocument(serializer, getRoot(), this, key);
    }

    // IO

    @Override
    public @NotNull Expect<Void, IOException> reinit(@NotNull Reader reader) {
        return Expect.compute(() -> {
            clear();
            Expect.compute(() -> serializer.deserialize(reader, this)).ifCaught(ex -> {
                throw new IOException(ex);
            });
        });
    }

    @Override
    public @NotNull Expect<Void, IOException> dump(@NotNull Writer writer) {
        return Expect.compute(() -> serializer.serialize(writer, this));
    }

    public void clear() {
        children.clear();
    }

    // serializer access

    Map<String, Object> children() {
        return children;
    }

    void put(String key, Object val) {
        children.put(key, val);
    }

}
