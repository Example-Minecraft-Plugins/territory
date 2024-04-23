package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryMemberConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@RequiredArgsConstructor
public class EntityDamageEntityListener implements Listener {
    private final TerritoryPlugin plugin;

    // Canceling PvP in territories where it is disabled
    @EventHandler
    private void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        Territory damagerTerritory = plugin.getTerritoryCache().getTerritory(damager.getLocation());
        Territory damagedTerritory = plugin.getTerritoryCache().getTerritory(damaged.getLocation());

        boolean damagerTerritoryPvp = damagerTerritory != null && !damagerTerritory.getTerritoryConfig().isPvpEnabled();
        boolean damagedTerritoryPvp = damagedTerritory != null && !damagedTerritory.getTerritoryConfig().isPvpEnabled();

        if (damagerTerritoryPvp || damagedTerritoryPvp) {
            event.setCancelled(true);
            damager.sendMessage("§cO PvP está desativado neste terreno.");
        }
    }

    // Canceling entity damage in territories from members who are not allowed to do so
    @EventHandler
    private void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getEntity() instanceof Player) return; // PvP is handled in another event

        Player player = (Player) event.getDamager();

        Territory territory = plugin.getTerritoryCache().getTerritory(player.getLocation());
        if (territory == null) return;

        TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canDamageEntities()) {
            event.setCancelled(true);
            player.sendMessage("§cVocê não tem permissão de atacar entidades neste terreno.");
        }
    }
}
