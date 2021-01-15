package eu.software4you.spigot.scheme;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SchemeData {
    private final LinkedHashMap<Location, BlockData> data;

    SchemeData(LinkedHashMap<Location, BlockData> data) {
        this.data = data;
    }

    public LinkedHashMap<Location, BlockData> getData() {
        return data;
    }

    public List<Block> getBlocks() {
        List<Block> li = new ArrayList<>();
        for (Location l : data.keySet())
            li.add(l.getBlock());
        return li;
    }

    public List<Location> getLocations() {
        return new ArrayList<>(data.keySet());
    }

    public void paste() {
        for (Location loc : data.keySet()) {
            loc.getBlock().setType(data.get(loc).getMaterial(), false);
            //loc.getBlock().setData((byte) data.get(loc).getDurability());
        }
    }
}
