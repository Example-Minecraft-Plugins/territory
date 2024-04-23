package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DesbanirSubCommand implements TerrenoSubCommand {
    private final TerritoryPlugin plugin;

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cInforme o jogador que deseja desbanir.");
            return false;
        }

        final Location location = player.getLocation();
        final int x = location.getBlockX();
        final int z = location.getBlockZ();

        Territory territory = plugin.getTerritoryCache().getTerritory(x, z);
        if (territory == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        if (!territory.getOwner().equals(player.getName())) {
            player.sendMessage("§cApenas o dono do terreno pode desbanir jogadores.");
            return true;
        }

        String playerName = args[1];
        if (!territory.getTerritoryConfig().isBanned(playerName)) {
            player.sendMessage("§cEste jogador não está banido do terreno.");
            return true;
        }

        territory.getTerritoryConfig().unbanPlayer(playerName);
        player.sendMessage(String.format("§aO jogador §f%s §afoi desbanido do terreno.", playerName));

        return true;
    }

    @Override
    public String getUsage() {
        return "§e/terreno desbanir <jogador>";
    }
}
