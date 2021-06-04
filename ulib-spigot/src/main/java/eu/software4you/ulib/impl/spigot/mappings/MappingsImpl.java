package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.spigot.mappings.JarMapping;
import eu.software4you.spigot.mappings.Mappings;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayOutputStream;

@Impl(Mappings.class)
final class MappingsImpl extends Mappings {
    @SneakyThrows
    @Override
    protected JarMapping load(VersionManifest manifest, String what) {
        RemoteResource res = manifest.getDownload(what);
        if (res == null)
            return null;

        val out = new ByteArrayOutputStream();
        res.download(out);
        return new MapRoot(out.toString());
    }
}
