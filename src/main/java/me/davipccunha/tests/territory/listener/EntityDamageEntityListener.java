package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryMemberConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

@RequiredArgsConstructor
public class EntityDamageEntityListener implements Listener {
    private final TerritoryPlugin plugin;

    // Canceling PvP in territories where it is disabled
    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;

        final Player damager = (Player) event.getDamager();
        final Player damaged = (Player) event.getEntity();

        final boolean cancel = this.handlePlayerDamagePlayer(damager, damaged);
        event.setCancelled(cancel);
    }

    // Canceling PvP in territories where it is disabled
    @EventHandler(priority = EventPriority.LOW)
    private void onProjectileDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Projectile)) return;

        final Player damaged = (Player) event.getEntity();

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

        final Player damager = (Player) ((Projectile) event.getDamager()).getShooter();

        final boolean cancel = this.handlePlayerDamagePlayer(damager, damaged);
        event.setCancelled(cancel);
    }

    // Canceling entity damage in territories from members who are not allowed to do so
    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getEntity() instanceof Player) return; // PvP is handled in another event

        final  Player player = (Player) event.getDamager();

        final boolean cancel = this.handlePlayerDamageEntity(player, event.getEntity());
        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onProjectileDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile)) return;

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

        final Player player = (Player) ((Projectile) event.getDamager()).getShooter();

        final boolean cancel = this.handlePlayerDamageEntity(player, event.getEntity());
        event.setCancelled(cancel);
    }

    // Breaking paintings does not fire the EntityDamageByEntityEvent, so we need to handle it separately
    @EventHandler(priority = EventPriority.LOW)
    private void onHangingBreak(HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();

        if (!(remover instanceof Player) && !(remover instanceof Projectile)) return;
        if (remover instanceof Projectile && !(((Projectile) remover).getShooter() instanceof Player)) return;

        final Player player = remover instanceof Projectile ? (Player) ((Projectile) remover).getShooter() : (Player) event.getRemover();

        if (player.hasPermission("territory.admin.damage")) return;

        final Territory territory = plugin.getTerritoryCache().getTerritory(event.getEntity().getLocation());
        if (territory == null) return;

        final TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canBuild()) {
            event.setCancelled(true);
            player.sendMessage("§cVocê não tem permissão de quebrar blocos neste terreno.");
        }
    }

    private boolean handlePlayerDamagePlayer(Player damager, Player damaged) {
        if (damager.hasPermission("territory.admin")) return false;

        final Territory damagerTerritory = plugin.getTerritoryCache().getTerritory(damager.getLocation());
        final Territory damagedTerritory = plugin.getTerritoryCache().getTerritory(damaged.getLocation());

        final boolean damagerTerritoryPvp = damagerTerritory != null && !damagerTerritory.getTerritoryConfig().isPvpEnabled();
        final boolean damagedTerritoryPvp = damagedTerritory != null && !damagedTerritory.getTerritoryConfig().isPvpEnabled();

        if (damagerTerritoryPvp || damagedTerritoryPvp) {
            damager.sendMessage("§cO PvP está desativado neste terreno.");
            return true;
        }

        return false;
    }

    private boolean handlePlayerDamageEntity(Player player, Entity entity) {
        if (player.hasPermission("territory.admin.damage")) return false;

        final Territory territory = plugin.getTerritoryCache().getTerritory(entity.getLocation());

        if (territory == null) return false;

        final TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(player.getName());

        if (memberConfig == null || !memberConfig.canDamageEntities()) {
            player.sendMessage("§cVocê não tem permissão de atacar entidades neste terreno.");
            return true;
        }

        if (entity instanceof ItemFrame && !memberConfig.canBuild()) {
            player.sendMessage("§cVocê não tem permissão de quebrar blocos neste terreno.");
            return true;
        }

        return false;
    }
}
