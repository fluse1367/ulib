package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.spigot.mappings.Mappings;
import eu.software4you.spigot.multiversion.MultiversionManager;
import eu.software4you.spigot.multiversion.Protocol;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.Tasks;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayOutputStream;

import static eu.software4you.ulib.ULibSpigotPlugin.getPlainMcVersion;

@Impl(value = Mappings.class, priority = 4999)
final class MappingsImpl extends Mappings {
    private final Loader<VanillaMapping> currentVanilla = new Loader<>(() -> {
        String ver = getPlainMcVersion();
        val manifest = LauncherMeta.getVersionManifest().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadVanilla(manifest);
    });
    private final Loader<BukkitMapping> currentBukkit = new Loader<>(() ->
            loadBukkit(getPlainMcVersion()));
    private final Loader<MixedMapping> currentMixed = new Loader<>(() -> {
        String ver = getPlainMcVersion();
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
        return currentVanilla.get();
    }

    @SneakyThrows
    @Override
    protected BukkitMapping loadBukkit(String version) {
        val prot = MultiversionManager.getVersion(version);
        if (prot.below(Protocol.v1_8_R1))
            return null; // no bukkit mappings before 1.8
        ULib.logger().fine(() -> "Loading Bukkit Mapping for " + version);
        val data = BuildDataMeta.loadBuildData(version);
        if (data == null)
            return null;

        val res = Tasks.await(() -> {
            val clOut = new ByteArrayOutputStream();
            IOUtil.write(data.getClassMappings().require(), clOut);
            return clOut;
        }, () -> {
            val memOut = new ByteArrayOutputStream();
            IOUtil.write(data.getMemberMappings().require(), memOut);
            return memOut;
        });

        return new BukkitMapping(res.get(0).toString(), res.get(1).toString(), prot);
    }

    @Override
    protected BukkitMapping getCurrentBukkit() {
        return currentBukkit.get();
    }

    @SneakyThrows
    @Override
    protected MixedMapping loadMixed(VersionManifest version) {
        val logger = ULib.logger();
        logger.fine(() -> "Loading mixed mappings for " + version.getId());
        val res = Tasks.await(() -> {
            val mapping = loadVanilla(version);
            logger.fine(() -> "Vanilla Mappings loaded!");
            return mapping;
        }, () -> {
            val mapping = loadBukkit(version.getId());
            logger.fine(() -> "Bukkit Mappings loaded!");
            return mapping;
        });

        val vm = res.get(0);
        val bm = res.get(1);
        if (!(vm instanceof VanillaMapping) || !(bm instanceof BukkitMapping))
            return null;
        logger.fine(() -> "Combining ...");
        return new MixedMapping((BukkitMapping) bm, (VanillaMapping) vm);
    }

    @Override
    protected MixedMapping getCurrentMixed() {
        return currentMixed.get();
    }
}
