package eu.software4you.ulib.test.configuration.yaml;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.test.configuration.SerializableObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class JsonSerializationTest {

    @Test
    public void testSerialization() {
        var serializableObject = new SerializableObject(42, "Hello World!");

        // serialize to string
        var json = JsonConfiguration.newJson();
        json.set("obj", serializableObject);
        var wr = new StringWriter();
        json.dump(wr);
        var serialized = wr.toString();

        // deserialize to object
        json.clear();
        json.reinit(new StringReader(serialized))
                .rethrowRE();
        var obj = json.get("obj").orElseThrow();

        Assert.assertEquals(serializableObject, obj);
    }
}
