package eu.software4you.minecraft.scheme;

import eu.software4you.math.Coordinate2D;
import eu.software4you.math.Coordinate3D;
import eu.software4you.math.MathUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Scheme {
    private final List<DynamicBlock> blocks = new ArrayList<>();

    Scheme(DynamicBlock... blocks) {
        this.blocks.addAll(Arrays.asList(blocks));
    }

    Scheme(List<DynamicBlock> blocks) {
        this.blocks.addAll(blocks);
    }

    public List<DynamicBlock> getBlocks() {
        return blocks;
    }

    public SchemeData getData(Location center) {
        return getData(center, 0);
    }

    public SchemeData getData(Location center, double rotationY) {
        LinkedHashMap<Location, BlockData> map = new LinkedHashMap<>();
        for (DynamicBlock d : blocks) {
            Coordinate3D c3 = d.getCoordinate3D();

            Coordinate2D c2 = MathUtils.rotateCoordinate2D(new Coordinate2D(c3.getX(), c3.getZ()), rotationY);

            c3 = new Coordinate3D(c2.getX(), c3.getY(), c2.getY());

            map.put(center.clone().add(c3.getX(), c3.getY(), c3.getZ()), d.getBlockData());
        }
        return new SchemeData(map);
    }
}
