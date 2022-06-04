package eu.software4you.ulib.test.minecraft.mapping;

import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import eu.software4you.ulib.minecraft.mappings.Mappings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

// test for bukkit server jar
// proprietary (mojang) source -> distributed (bukkit) jar
public class MixedMappingTest {

    @Test
    public void testMixedMappings() {

        var manifest = VersionsMeta.getCurrent().get("1.18.2")
                .orElseThrow();

        var mapping = Mappings.loadMixedMapping(manifest)
                .orElseThrow();

        // fetch class mapping for Level (World)
        var clazz = mapping.fromMapped("net.minecraft.world.level.World")
                .orElseThrow();
        assertSame(clazz, mapping.fromSource("net.minecraft.world.level.Level").orElseThrow());

        // fetch method mapping for isLoaded(BlockPos)
        var method = clazz.methodFromSource("isLoaded").orElseThrow();
        assertEquals(method.mappedName(), "n");


        // test param
        var param0 = method.parameterTypes()[0];
        assertSame(param0, mapping.fromMapped("net.minecraft.core.BlockPosition").orElseThrow());
    }

}
