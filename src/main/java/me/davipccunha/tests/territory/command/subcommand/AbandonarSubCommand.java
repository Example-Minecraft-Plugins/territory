package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryUser;
import me.davipccunha.utils.cache.RedisCache;
import me.davipccunha.utils.inventory.InteractiveInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

@RequiredArgsConstructor
public class AbandonarSubCommand implements TerrenoSubCommand {
    private final TerritoryPlugin plugin;

    @Override
    public boolean execute(Player player, String[] args) {

        final Location location = player.getLocation();
        final int x = location.getBlockX();
        final int z = location.getBlockZ();

        final Territory territory = plugin.getTerritoryCache().getTerritory(x, z);
        if (territory == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        final RedisCache<TerritoryUser> cache = plugin.getUserRedisCache();

        if (player.hasPermission("territory.admin.delete")) {
            final String owner = territory.getOwner().toLowerCase();
            plugin.getTerritoryCache().remove(territory);

            final TerritoryUser territoryUser = cache.get(owner);
            territoryUser.removeTerritory(territory.getCenter());

            cache.add(owner, territoryUser);

            if (territoryUser.getTerritories().isEmpty())
                cache.remove(owner);

            player.sendMessage("§aVocê abandonou o terreno com sucesso.");
            return true;
        }

        if (!territory.getOwner().equals(player.getName())) {
            player.sendMessage("§cApenas o dono do terreno pode abandoná-lo.");
            return true;
        }

        final Map<String, String> confirmNBTTags = Map.of(
                "action", "confirm-abandon",
                "location", territory.getCenter().serialize()
        );

        final Map<String, String> cancelNBTTags = Map.of(
                "action", "cancel-abandon",
                "location", territory.getCenter().serialize()
        );

        final Inventory confirmationInventory = InteractiveInventory.createConfirmationInventory(
                "Abandonar Terreno", confirmNBTTags, cancelNBTTags
        );

        player.openInventory(confirmationInventory);

        return true;
    }

    @Override
    public String getUsage() {
        return "/terreno abandonar";
    }
}
