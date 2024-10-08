package me.davipccunha.tests.territory.command;

import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.command.subcommand.*;
import me.davipccunha.utils.messages.ErrorMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TerrenoCommand implements CommandExecutor {
    private static String COMMAND_USAGE;
    private final Map<String, TerrenoSubCommand> subCommands = new HashMap<>();

    public TerrenoCommand(TerritoryPlugin plugin) {
        this.loadSubCommands(plugin);

        this.updateUsage();
    }

    private void updateUsage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/terreno [");
        for (String subCommand : this.subCommands.keySet()) {
            stringBuilder.append(subCommand).append(" | ");
        }
        stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
        stringBuilder.append("]");

        COMMAND_USAGE = stringBuilder.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ErrorMessages.EXECUTOR_NOT_PLAYER.getMessage());
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            Bukkit.dispatchCommand(player, "terreno info");
            return true;
        }

        final TerrenoSubCommand subCommand = this.subCommands.get(args[0]);

        if (subCommand == null) {
            sender.sendMessage(ErrorMessages.SUBCOMMAND_NOT_FOUND.getMessage());
            sender.sendMessage("§cUso: " + COMMAND_USAGE);
            return false;
        }

        if (!subCommand.execute(player, args)) {
            sender.sendMessage("§cUso: " + subCommand.getUsage());
            return false;
        }

        return true;
    }

    private void loadSubCommands(TerritoryPlugin plugin) {
        this.subCommands.put("adquirir", new AdquirirSubCommand(plugin));
        this.subCommands.put("abandonar", new AbandonarSubCommand(plugin));
        this.subCommands.put("demarcar", new DemarcarSubCommand(plugin));
        this.subCommands.put("info", new InfoSubCommand(plugin));
        this.subCommands.put("banir", new BanirSubCommand(plugin));
        this.subCommands.put("desbanir", new DesbanirSubCommand(plugin));
        this.subCommands.put("adicionar", new AdicionarSubCommand(plugin));
        this.subCommands.put("remover", new RemoverSubCommand(plugin));
    }
}
