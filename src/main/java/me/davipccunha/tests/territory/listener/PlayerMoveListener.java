package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.cache.TerritoryCache;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryBlock;
import me.davipccunha.tests.territory.model.TerritoryConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

@RequiredArgsConstructor
public class PlayerMoveListener implements Listener {
    private final TerritoryPlugin plugin;
    private final HashMap<String, TerritoryBlock> playerLastTerritory = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerMove(PlayerMoveEvent event) {
        handleTerritoryEnter(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        handleTerritoryEnter(event);
    }

    private boolean isValidDestination(String playerName, int x, int z) {
        final TerritoryCache cache = plugin.getTerritoryCache();

        final Territory territory = cache.getTerritory(x, z);
        final TerritoryConfig territoryConfig = territory.getTerritoryConfig();
        if (territoryConfig == null) return true;

        return territoryConfig.hasMember(playerName)
                || (territoryConfig.isAccessible() && !territoryConfig.isBanned(playerName));
    }

    private void handleTerritoryEnter(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;

        final Location location = event.getTo();
        if (location == null) return;

        // PlayerMoveEvent fires even if the player changes the pitch or yaw, so we check the actual position change to avoid unnecessary calls
        if (location.getBlockX() == event.getFrom().getBlockX() && location.getBlockZ() == event.getFrom().getBlockZ()) return;

        final TerritoryCache cache = plugin.getTerritoryCache();
        final Territory territory = cache.getTerritory(location);

        if (territory == null) {
            playerLastTerritory.remove(player.getName());
            return;
        }

        // This verification avoids further checks if the player is still in the same territory
        if (playerLastTerritory.get(player.getName()) == territory.getCenter()) return;

        if (isValidDestination(player.getName(), location.getBlockX(), location.getBlockZ())) {
            playerLastTerritory.put(player.getName(), territory.getCenter());
            return;
        }

        player.sendMessage("§cVocê não pode entrar neste terreno.");
        event.setCancelled(true);
    }
}
