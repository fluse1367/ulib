package eu.software4you.ulib.test.configuration;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class YamlSerializationTest {


    @Test
    public void testFreshDocument() {
        testSerialization(YamlConfiguration.newYaml());
    }

    @Test
    public void testEmptyDocument() throws Exception {
        var doc = YamlConfiguration.loadYaml(new StringReader(""))
                .orElseRethrow();

        testSerialization(doc);
    }

    private void testSerialization(YamlConfiguration yaml) {
        var serializableObject = new SerializableObject(42, "Hello World!");


        // serialize to string
        yaml.set("obj", serializableObject);
        StringWriter wr = new StringWriter();
        yaml.dump(wr).rethrowRE();
        String serialized = wr.toString();


        // deserialize to object
        yaml.clear();
        yaml.reinit(new StringReader(serialized))
                .rethrowRE();
        var obj = yaml.get("obj").orElseThrow();

        Assert.assertEquals(serializableObject, obj);
    }


}
