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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {
    private final TerritoryPlugin plugin;

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (player == null || block == null) return;

        if (player.hasPermission("territory.admin")) return;

        final Territory territory = plugin.getTerritoryCache().getTerritory(block.getLocation());
        if (territory == null) return;

        final boolean isContainer = block.getState() instanceof InventoryHolder;
        final boolean isDoor = block.getType().toString().contains("DOOR")
                || block.getType().toString().contains("GATE");

        final boolean isAnvil = block.getType().toString().contains("ANVIL");

        if (!isContainer && !isDoor && !isAnvil) return;

        final TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canInteract()) {
            player.sendMessage("§cVocê não pode interagir com blocos neste terreno.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();

        if (player == null) return;

        if (player.hasPermission("territory.admin")) return;

        final Territory territory = plugin.getTerritoryCache().getTerritory(event.getRightClicked().getLocation());
        if (territory == null) return;

        final TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canInteract()) {
            player.sendMessage("§cVocê não pode interagir com entidades neste terreno.");
            event.setCancelled(true);
        }
    }
}
