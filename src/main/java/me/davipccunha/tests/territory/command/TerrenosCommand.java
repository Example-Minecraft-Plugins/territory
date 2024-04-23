package me.davipccunha.tests.territory.command;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.factory.view.TerritoriesManagerGUI;
import me.davipccunha.tests.territory.model.TerritoryUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class TerrenosCommand implements CommandExecutor {
    private final TerritoryPlugin plugin;
    private static final String COMMAND_USAGE = "§e/terrenos";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem executar este comando.");
            return true;
        }

        Player player = (Player) sender;

        final TerritoryUser territoryUser = plugin.getUserRedisCache().get(player.getName());
        if (territoryUser == null || territoryUser.getTerritories().isEmpty()) {
            player.openInventory(TerritoriesManagerGUI.createEmptyTerritoriesGUI());
            return true;
        }

        Inventory inventory = TerritoriesManagerGUI.createUserTerritoriesGUI(territoryUser);
        player.openInventory(inventory);

        return true;
    }
}
