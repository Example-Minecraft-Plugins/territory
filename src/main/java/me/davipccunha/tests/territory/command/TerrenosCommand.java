package me.davipccunha.tests.territory.command;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.factory.view.TerritoriesManagerGUI;
import me.davipccunha.tests.territory.model.TerritoryUser;
import me.davipccunha.utils.messages.ErrorMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class TerrenosCommand implements CommandExecutor {
    private final TerritoryPlugin plugin;
    private static final String COMMAND_USAGE = "/terrenos";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ErrorMessages.EXECUTOR_NOT_PLAYER.getMessage());
            return true;
        }

        final Player player = (Player) sender;

        final TerritoryUser territoryUser = plugin.getUserRedisCache().get(player.getName().toLowerCase());
        if (territoryUser == null || territoryUser.getTerritories().isEmpty()) {
            player.openInventory(TerritoriesManagerGUI.createEmptyTerritoriesGUI());
            return true;
        }

        final Inventory inventory = TerritoriesManagerGUI.createUserTerritoriesGUI(territoryUser);
        player.openInventory(inventory);

        return true;
    }
}
