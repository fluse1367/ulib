package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.api.configuration.yaml.ExtYamlSub;
import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;

public final class YamlSubProvider implements YamlSub.Provider {
    private final YamlSerializer yaml = YamlSerializer.getInstance();

    @Override
    public Class<? extends ExtYamlSub> type() {
        return ExtYamlSub.class;
    }

    @Override
    public ExtYamlSub get() {
        return yaml.createNew();
    }
}
