package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.spigot.mappings.Mappings;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.ULibSpigotPlugin;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Impl(Mappings.class)
final class MappingsImpl extends Mappings {
    private final Loader<VanillaMapping> current = new Loader<>(() -> {
        String ver = ULibSpigotPlugin.getInstance().getPlainMcVersion();
        val manifest = LauncherMeta.getVersionManifest().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadVanilla(manifest);
    });
    private final Loader<Map<String, BuildDataMeta>> bukkitBuildData = new Loader<>(BuildDataMeta::loadBuildData);
    private final Loader<BukkitMapping> currentBukkit = new Loader<>(() ->
            loadBukkit(ULibSpigotPlugin.getInstance().getPlainMcVersion()));
    private final Loader<MixedMapping> currentMixed = new Loader<>(() -> {
        String ver = ULibSpigotPlugin.getInstance().getPlainMcVersion();
        val manifest = LauncherMeta.getVersionManifest().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadMixed(manifest);
    });

    @SneakyThrows
    @Override
    protected VanillaMapping loadVanilla(VersionManifest version) {
        if (version == null)
            return null;
        ULib.logger().fine(() -> "Loading Vanilla Mappings for " + version.getId());
        val mapping = version.getDownload("server_mappings");
        if (mapping == null)
            return null;

        val out = new ByteArrayOutputStream();
        mapping.download(out);
        return new VanillaMapping(out.toString());
    }

    @Override
    protected VanillaMapping getCurrentVanilla() {
        return current.get();
    }

    @SneakyThrows
    @Override
    protected BukkitMapping loadBukkit(String version) {
        ULib.logger().fine(() -> "Init Bukkit Mappings");
        val map = bukkitBuildData.get();
        if (!map.containsKey(version))
            return null;
        ULib.logger().fine(() -> "Loading Bukkit Mapping for " + version);
        val data = map.get(version);

        val clOut = new ByteArrayOutputStream();
        IOUtil.write(data.getClassMappings().request(), clOut);

        val memOut = new ByteArrayOutputStream();
        IOUtil.write(data.getMemberMappings().request(), memOut);

        return new BukkitMapping(clOut.toString(), memOut.toString());
    }

    @Override
    protected BukkitMapping getCurrentBukkit() {
        return currentBukkit.get();
    }

    @Override
    protected MixedMapping loadMixed(VersionManifest version) {
        val logger = ULib.logger();
        logger.fine(() -> "Loading mixed mappings for " + version.getId());
        val vm = loadVanilla(version);
        if (vm == null)
            return null;
        logger.fine(() -> "Vanilla Mappings loaded!");
        val bm = loadBukkit(version.getId());
        if (bm == null)
            return null;
        logger.fine(() -> "Bukkit Mappings loaded! Combining ...");
        return new MixedMapping(bm, vm);
    }

    @Override
    protected MixedMapping getCurrentMixed() {
        return currentMixed.get();
    }
}
