package eu.software4you.ulib.impl.minecraft.launchermeta;

import com.google.gson.JsonParser;
import eu.software4you.http.HttpUtil;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import lombok.SneakyThrows;

import java.io.InputStreamReader;

@Impl(value = LauncherMeta.class, priority = 5000)
final class LauncherMetaImpl extends LauncherMeta {
    private final Loader<Meta> loader = new Loader<>(() -> new Meta(JsonParser.parseReader(new InputStreamReader(
            HttpUtil.getContent("https://launchermeta.mojang.com/mc/game/version_manifest.json")
    )).getAsJsonObject()));

    @SneakyThrows
    @Override
    protected VersionsMeta getVersionManifest0() {
        return loader.get();
    }

    @Override
    protected void reset0() {
        if (loader.isRunning())
            throw new IllegalStateException("Cannot reset while loading");
        loader.reset();
    }
}
