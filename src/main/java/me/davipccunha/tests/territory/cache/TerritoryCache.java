package me.davipccunha.tests.territory.cache;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.util.TerritoryUtil;
import me.davipccunha.utils.world.WorldUtils;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class TerritoryCache {
    // This cache stores the territories by chunk hash, representing all territories that has at least one block in that chunk
    // Doing it this way allows us to perform a quicker search for territories instead of iterating over all territories
    private final HashMap<Long, Set<Territory>> cache = new HashMap<>();
    private final TerritoryPlugin plugin;

    public void add(Territory territory) {
        Collection<Long> hashes = TerritoryUtil.getTerritoryChunksHashes(territory.getTerritoryArea());

        for (long hash : hashes) {
            Set<Territory> territories = this.cache.computeIfAbsent(hash, k -> new HashSet<>());
            territories.add(territory);
        }

        this.plugin.getTerritoryRedisCache().add(territory.getCenter().serialize(), territory);
    }

    public void remove(Territory territory) {
        if (!this.hasTerritory(territory)) return;
        Collection<Long> hashes = TerritoryUtil.getTerritoryChunksHashes(territory.getTerritoryArea());

        for (long hash : hashes) {
            Set<Territory> territories = this.cache.get(hash);
            territories.remove(territory);
            if (territories.isEmpty()) this.cache.remove(hash);
        }

        this.plugin.getTerritoryRedisCache().remove(territory.getCenter().serialize());
    }

    public boolean hasTerritory(Territory territory) {
        return this.getTerritory(territory.getCenter().getX(), territory.getCenter().getZ()) != null;
    }

    public Set<Territory> getTerritories(long hash) {
        return this.cache.get(hash);
    }

    public Set<Territory> getTerritories(int x, int z) {
        return this.getTerritories(WorldUtils.chunkHash(x, z));
    }

    public Territory getTerritory(int x, int z) {
        Set<Territory> territories = this.getTerritories(x, z);
        return TerritoryUtil.getPreciseTerritory(territories, x, z);
    }

    public Territory getTerritory(Location location) {
        return this.getTerritory(location.getBlockX(), location.getBlockZ());
    }

    public void load() {
        this.plugin.getTerritoryRedisCache().getValues().forEach(this::add);
    }
}
