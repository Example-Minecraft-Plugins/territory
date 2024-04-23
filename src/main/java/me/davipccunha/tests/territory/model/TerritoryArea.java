package me.davipccunha.tests.territory.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class TerritoryArea {
    private final String worldName;
    private int minX, maxX, minZ, maxZ;

    public TerritoryArea(String worldName, int centerX, int centerZ, int size) {
        this.worldName = worldName;
        this.minX = (centerX - size);
        this.maxX = (centerX + size);
        this.minZ = (centerZ - size);
        this.maxZ = (centerZ + size);
    }

    public TerritoryArea(Location location, int size) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockZ(), size);
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX
                && z >= minZ && z <= maxZ;
    }

    public Collection<TerritoryBlock> getCorners() {
        return Collections.unmodifiableCollection(Arrays.asList(
                new TerritoryBlock(worldName, minX, minZ),
                new TerritoryBlock(worldName, minX, maxZ),
                new TerritoryBlock(worldName, maxX, minZ),
                new TerritoryBlock(worldName, maxX, maxZ)
        ));
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) -> (%d, %d)", minX, minZ, maxX, maxZ);
    }
}
