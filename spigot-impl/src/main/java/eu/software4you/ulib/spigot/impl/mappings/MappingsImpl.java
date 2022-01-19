package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.core.api.utils.LazyValue;
import eu.software4you.ulib.core.impl.Tasks;
import eu.software4you.ulib.core.impl.UnsafeOperations;
import eu.software4you.ulib.minecraft.api.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.api.launchermeta.VersionManifest;
import eu.software4you.ulib.spigot.api.mappings.Mappings;
import eu.software4you.ulib.spigot.api.multiversion.MultiversionManager;
import eu.software4you.ulib.spigot.api.multiversion.Protocol;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;

import static eu.software4you.ulib.spigot.impl.PluginSubst.getPlainMcVersion;

final class MappingsImpl extends Mappings {
    private final LazyValue<VanillaMapping> currentVanilla = new LazyValue<>(() -> {
        String ver = getPlainMcVersion();
        var manifest = LauncherMeta.getVersionManifest().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadVanilla(manifest);
    });
    private final LazyValue<BukkitMapping> currentBukkit = new LazyValue<>(() ->
            loadBukkit(getPlainMcVersion()));
    private final LazyValue<MixedMapping> currentMixed = new LazyValue<>(() -> {
        String ver = getPlainMcVersion();
        var manifest = LauncherMeta.getVersionManifest().get(ver);
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
        var mapping = version.getDownload("server_mappings");
        if (mapping == null)
            return null;

        var out = new ByteArrayOutputStream();
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
        var protocol = MultiversionManager.getVersion(version);
        if (!UnsafeOperations.comply(protocol == Protocol.UNKNOWN, "Bukkit Mappings loading",
                String.format("version '%s' unknown", version),
                String.format("Ignoring version '%s' being unknown", version)) && protocol.below(Protocol.v1_8_R1)) {
            ULib.logger().fine(() -> "(Bukkit Mappings loading) Cannot comply: version '" + version + "' is below '1.8'.");
            return null; // no bukkit mappings before 1.8
        }

        ULib.logger().fine(() -> "Loading Bukkit Mapping for " + version);
        var data = BuildDataMeta.loadBuildData(version);
        if (data == null)
            return null;

        var res = Tasks.await(() -> {
            var clOut = new ByteArrayOutputStream();
            IOUtil.write(data.getClassMappings().require(), clOut);
            return clOut;
        }, () -> {
            var memOut = new ByteArrayOutputStream();
            IOUtil.write(data.getMemberMappings().require(), memOut);
            return memOut;
        });

        return new BukkitMapping(res.get(0).toString(), res.get(1).toString(), protocol);
    }

    @Override
    protected BukkitMapping getCurrentBukkit() {
        return currentBukkit.get();
    }

    @SneakyThrows
    @Override
    protected MixedMapping loadMixed(VersionManifest version) {
        var logger = ULib.logger();
        logger.fine(() -> "Loading mixed mappings for " + version.getId());
        var res = Tasks.await(() -> {
            var mapping = loadVanilla(version);
            logger.fine(() -> "Vanilla Mappings loaded!");
            return mapping;
        }, () -> {
            var mapping = loadBukkit(version.getId());
            logger.fine(() -> "Bukkit Mappings loaded!");
            return mapping;
        });

        var vm = res.get(0);
        var bm = res.get(1);
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
