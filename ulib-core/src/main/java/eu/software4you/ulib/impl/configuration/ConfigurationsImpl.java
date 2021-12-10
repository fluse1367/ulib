package eu.software4you.ulib.impl.configuration;

import eu.software4you.ulib.core.api.configuration.Configurations;
import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;
import eu.software4you.ulib.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.inject.Impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

@Impl(Configurations.class)
final public class ConfigurationsImpl extends Configurations {

    private final YamlSerializer yaml = YamlSerializer.getInstance();

    @Override
    protected YamlSub newYaml0() {
        return yaml.createNew();
    }

    @Override
    protected YamlSub loadYaml0(Reader reader) throws IOException {
        var sub = newYaml0();
        sub.load(reader);
        return sub;
    }

    @Override
    protected void saveYaml0(YamlSub yaml, Writer writer) throws IOException {
        yaml.save(writer);
    }
}
