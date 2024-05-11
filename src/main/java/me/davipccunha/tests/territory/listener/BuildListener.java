package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryMemberConfig;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@RequiredArgsConstructor
public class BuildListener implements Listener {
    private final TerritoryPlugin plugin;

    @EventHandler(priority = EventPriority.LOW)
    private void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player == null || block == null) return;

        if (!verifyPermission(block, player, "§cVocê não tem permissão de quebrar blocos neste terreno."))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (player == null || block == null) return;

        if (!verifyPermission(block, player, "§cVocê não tem permissão de colocar blocos neste terreno."))
            event.setCancelled(true);
    }

    private boolean verifyPermission(Block block, Player player, String denialMessage) {
        if (player.hasPermission("territory.admin.build")) return true;

        final Territory territory = plugin.getTerritoryCache().getTerritory(block.getLocation());
        if (territory == null) return true;

        final TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canBuild()) {
            player.sendMessage(denialMessage);
            return false;
        }

        return true;
    }
}
