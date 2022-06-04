package eu.software4you.ulib.test.minecraft.mapping;

import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import eu.software4you.ulib.minecraft.mappings.Mappings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

// test for vanilla server jar
// proprietary source -> distributed jar
public class VanillaMappingTest {

    @Test
    public void testVanillaMappings() {
        var manifest = VersionsMeta.getCurrent().get("1.16.5")
                .orElseThrow();

        var mapping = Mappings.loadVanillaServerMapping(manifest)
                .orElseThrow();

        // fetch class mapping for Level (World)
        var clazz = mapping.fromSource("net.minecraft.world.level.Level")
                .orElseThrow();
        assertSame(clazz, mapping.fromMapped("brx").orElseThrow());

        // fetch method mapping for isLoaded(BlockPos)
        var method = clazz.methodFromSource("isLoaded").orElseThrow();
        assertEquals(method.mappedName(), "p");

        // test param
        var param0 = method.parameterTypes()[0];
        assertSame(param0, mapping.fromSource("net.minecraft.core.BlockPos").orElseThrow());
        assertEquals(param0.mappedName(), "fx");

    }

}
