package eu.software4you.minecraft.scheme;

import eu.software4you.math.Coordinate3D;

public class DynamicBlock {
    private final Coordinate3D coordinate3D;
    private final BlockData blockData;

    DynamicBlock(Coordinate3D coordinate3D, BlockData blockData) {
        this.coordinate3D = coordinate3D;
        this.blockData = blockData;
    }

    public Coordinate3D getCoordinate3D() {
        return coordinate3D;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
