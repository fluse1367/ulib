package eu.software4you.ulib.test.configuration.yaml;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.test.configuration.SerializableObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

public class YamlSerializationTest {


    @Test
    public void testSerialization() {
        var serializableObject = new SerializableObject(42, "Hello World!");


        // serialize to string
        var yaml = YamlConfiguration.newYaml();
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
