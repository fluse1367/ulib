package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.impl.Tasks;
import eu.software4you.ulib.core.impl.UnsafeOperations;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.LazyValue;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import eu.software4you.ulib.spigot.util.Protocol;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static eu.software4you.ulib.spigot.impl.PluginSubst.getPlainMcVersion;

public final class MappingsImpl {
    private static final LazyValue<VanillaMapping> currentVanilla = new LazyValue<>(() -> {
        String ver = getPlainMcVersion();
        var manifest = VersionsMeta.getCurrent().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadVanilla(manifest);
    });
    private static final LazyValue<BukkitMapping> currentBukkit = new LazyValue<>(() ->
            loadBukkit(getPlainMcVersion()));
    private static final LazyValue<MixedMapping> currentMixed = new LazyValue<>(() -> {
        String ver = getPlainMcVersion();
        var manifest = VersionsMeta.getCurrent().get(ver);
        if (manifest == null)
            throw new IllegalStateException(String.format("launchermeta.mojang.com: Unknown Server Version (%s)", ver));
        return loadMixed(manifest);
    });

    @SneakyThrows
    public static VanillaMapping loadVanilla(VersionManifest version) {
        if (version == null)
            return null;
        var mapping = version.getDownload("server_mappings");
        if (mapping == null)
            return null;

        var out = new ByteArrayOutputStream();
        mapping.download(out);
        return new VanillaMapping(out.toString());
    }

    public static VanillaMapping getCurrentVanilla() {
        return currentVanilla.get();
    }

    @SneakyThrows
    public static BukkitMapping loadBukkit(String version) {
        var protocol = Protocol.of(version);
        if (!UnsafeOperations.comply(protocol == Protocol.UNKNOWN, "Bukkit Mappings loading",
                String.format("version '%s' unknown", version),
                String.format("Ignoring version '%s' being unknown", version)) && protocol.below(Protocol.v1_8_R1)) {
            return null; // no bukkit mappings before 1.8
        }

        var data = BuildDataMeta.loadBuildData(version);
        if (data == null)
            return null;

        var res = Tasks.await(() -> {
            try (var in = data.getClassMappings().require().orElseThrow();
                 var clOut = new ByteArrayOutputStream()) {
                IOUtil.write(in, clOut);
                return clOut;
            }
        }, () -> {
            var mm = data.getMemberMappings();
            if (mm == null)
                return null;
            try (var in = mm.require().orElseThrow();
                 var memOut = new ByteArrayOutputStream()) {
                IOUtil.write(in, memOut);
                return memOut;
            }
        });

        return new BukkitMapping(res.get(0).toString(),
                Optional.ofNullable(res.get(1))
                        .map(ByteArrayOutputStream::toString)
                        .orElse(null),
                protocol);
    }

    public static BukkitMapping getCurrentBukkit() {
        return currentBukkit.get();
    }

    @SneakyThrows
    public static MixedMapping loadMixed(VersionManifest version) {
        var res = Tasks.await(
                () -> loadVanilla(version),
                () -> loadBukkit(version.getId())
        );

        var vm = res.get(0);
        var bm = res.get(1);
        if (!(vm instanceof VanillaMapping) || !(bm instanceof BukkitMapping))
            return null;
        return new MixedMapping((BukkitMapping) bm, (VanillaMapping) vm);
    }

    public static MixedMapping getCurrentMixed() {
        return currentMixed.get();
    }
}
