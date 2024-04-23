package me.davipccunha.tests.territory.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.davipccunha.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class TerritoryBlock {
    private final String worldName;
    private final int x, z;

    public TerritoryBlock(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockZ());
    }

    public Location toLocation() {
        final World world = Bukkit.getWorld(this.worldName);
        return new Location(world, this.x + 0.5, WorldUtils.getHighestY(this.worldName, this.x, this.z), this.z + 0.5);
    }

    public Location toLocation(float pitch, float yaw) {
        final Location location = toLocation();
        location.setPitch(pitch);
        location.setYaw(yaw);

        return location;
    }

    @Override
    public String toString() {
        return String.format("%s @ (%d, y, %d)", worldName, x, z);
    }

    public String serialize() {
        return String.format("%s;%d;%d", worldName, x, z);
    }

    public static TerritoryBlock deserialize(String serialized) {
        String[] parts = serialized.split(";");
        return new TerritoryBlock(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
}