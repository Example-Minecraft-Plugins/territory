package me.davipccunha.tests.territory.command.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface TerrenoSubCommand {
    boolean execute(Player player, String[] args);

    String getUsage();
}
