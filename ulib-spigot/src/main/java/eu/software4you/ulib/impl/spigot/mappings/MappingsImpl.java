package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.spigot.mappings.Mappings;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.ULibSpigotPlugin;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayOutputStream;

@Impl(Mappings.class)
final class MappingsImpl extends Mappings {
    private final Loader<eu.software4you.spigot.mappings.VanillaMapping> current = new Loader<>(() -> {
        String ver = ULibSpigotPlugin.getInstance().getPlainMcVersion();
        val manifest = LauncherMeta.getVersionManifest().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return Mappings.loadVanillaServerMappings(manifest);
    });

    @SneakyThrows
    @Override
    protected VanillaMapping loadVanilla(RemoteResource mapping) {
        if (mapping == null)
            return null;

        val out = new ByteArrayOutputStream();
        mapping.download(out);
        return new VanillaMapping(out.toString());
    }

    @Override
    protected eu.software4you.spigot.mappings.VanillaMapping getCurrentVanilla() {
        return current.get();
    }
}
