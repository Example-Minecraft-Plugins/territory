package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.model.Territory;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DemarcarSubCommand implements TerrenoSubCommand {
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

        territory.showBorders(plugin, player);
        player.sendMessage("§aA barreira mostra a demarcação do terreno.");

        return true;
    }

    @Override
    public String getUsage() {
        return "/terreno demarcar";
    }
}
