package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.cache.TerritoryCache;
import me.davipccunha.tests.territory.handler.TerritoryManagerHandler;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryBlock;
import me.davipccunha.tests.territory.model.TerritoryInput;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public class AsyncPlayerChatListener implements Listener {
    private final TerritoryPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();

        if (!plugin.getAwaitingTerritoryInput().containsKey(name)) return;
        final TerritoryInput input = plugin.getAwaitingTerritoryInput().get(name);

        event.setCancelled(true);

        plugin.getAwaitingTerritoryInput().remove(event.getPlayer().getName());

        final String targetName = event.getMessage();

        final TerritoryCache cache = plugin.getTerritoryCache();
        final String action = input.getAction();
        final TerritoryBlock center = input.getCenter();
        final Territory territory = cache.getTerritory(center.getX(), center.getZ());

        switch (action) {
            case "addMember":
                TerritoryManagerHandler.handleAddMemberInput(cache, territory, player, targetName);
                break;
            case "banPlayer":
                TerritoryManagerHandler.handleBanPlayerInput(cache, territory, player, targetName);
                break;
        }
    }
}
