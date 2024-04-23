package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AbandonarSubCommand implements TerrenoSubCommand {
    private final TerritoryPlugin plugin;

    @Override
    public boolean execute(Player player, String[] args) {

        final Location location = player.getLocation();
        final int x = location.getBlockX();
        final int z = location.getBlockZ();

        Territory territory = plugin.getTerritoryCache().getTerritory(x, z);
        if (territory == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!territory.getOwner().equals(player.getName())) {
            player.sendMessage("§cApenas o dono do terreno pode abandoná-lo.");
            return true;
        }

        plugin.getTerritoryCache().remove(territory);

        final TerritoryUser territoryUser = plugin.getUserRedisCache().get(player.getName());
        territoryUser.removeTerritory(territory.getCenter());

        plugin.getUserRedisCache().add(player.getName(), territoryUser);

        if (territoryUser.getTerritories().isEmpty())
            plugin.getUserRedisCache().remove(player.getName());

        player.sendMessage("§aVocê abandonou o terreno com sucesso.");

        return true;
    }

    @Override
    public String getUsage() {
        return "§e/terreno abandonar";
    }
}
