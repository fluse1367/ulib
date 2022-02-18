package eu.software4you.ulib.minecraft.impl.launchermeta;

import com.google.gson.JsonParser;
import eu.software4you.ulib.core.api.http.HttpUtil;
import eu.software4you.ulib.core.api.util.value.LazyValue;
import eu.software4you.ulib.minecraft.api.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.api.launchermeta.VersionsMeta;
import lombok.SneakyThrows;

import java.io.InputStreamReader;

public final class LauncherMetaImpl extends LauncherMeta {
    private final LazyValue<Meta> meta = new LazyValue<>(() -> new Meta(JsonParser.parseReader(new InputStreamReader(
            HttpUtil.getContent("https://launchermeta.mojang.com/mc/game/version_manifest.json")
    )).getAsJsonObject()));

    @SneakyThrows
    @Override
    protected VersionsMeta getVersionManifest0() {
        return meta.get();
    }

    @Override
    protected void reset0() {
        if (meta.isRunning())
            throw new IllegalStateException("Cannot reset while loading");
        meta.reset();
    }
}
