package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.factory.view.TerritoryConfigGUI;
import me.davipccunha.tests.territory.model.Territory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class InfoSubCommand implements TerrenoSubCommand {
    private final TerritoryPlugin plugin;

    @Override
    public boolean execute(Player player, String[] args) {
        Territory territory = plugin.getTerritoryCache().getTerritory(player.getLocation());
        if (territory == null) {
            player.sendMessage("§cVocê não está em um terreno.");
            return true;
        }

        Inventory territoryGUI = TerritoryConfigGUI.createTerritoryConfigGUI(territory);
        player.openInventory(territoryGUI);

        return true;
    }

    @Override
    public String getUsage() {
        return "/terreno info";
    }
}
