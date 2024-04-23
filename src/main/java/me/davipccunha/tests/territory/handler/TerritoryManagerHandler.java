package me.davipccunha.tests.territory.handler;

import me.davipccunha.tests.territory.cache.TerritoryCache;
import me.davipccunha.tests.territory.model.Territory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TerritoryManagerHandler {

    public static void handleAddMemberInput(TerritoryCache cache, Territory territory, Player player, String targetName) {
        if (!handleTerritoryInput(territory, player, targetName)) return;

        if (territory.getTerritoryConfig().hasMember(targetName)) {
            player.sendMessage("§cEste jogador já está adicionado ao terreno.");
            return;
        }

        // TODO: Limiting the players to 28 due to the GUI. Should add multiple pages to the GUI in the future
        if (territory.getTerritoryConfig().getMembersCount() > 28) {
            player.sendMessage("§cEste terreno atingiu o limite máximo de 28 membros.");
            return;
        }

        territory.getTerritoryConfig().addMember(targetName);
        cache.add(territory);
        player.sendMessage(String.format("§aO jogador §f%s §afoi adicionado ao terreno.", targetName));
    }

    public static void handleBanPlayerInput(TerritoryCache cache, Territory territory, Player player, String targetName) {
        if (!handleTerritoryInput(territory, player, targetName)) return;

        if (territory.getOwner().equals(targetName)) {
            player.sendMessage("§cVocê não pode banir o dono do terreno.");
            return;
        }

        if (territory.getTerritoryConfig().isBanned(targetName)) {
            player.sendMessage("§cEste jogador já está banido do terreno.");
            return;
        }

        // TODO: Limiting the players to 28 due to the GUI. Should add multiple pages to the GUI in the future
        if (territory.getTerritoryConfig().getBannedPlayers().size() > 28) {
            player.sendMessage("§cEste terreno atingiu o limite máximo de 28 jogadores banidos.");
            return;
        }

        // TODO: If banned player is online, kick him from the territory
        territory.getTerritoryConfig().banPlayer(targetName);
        cache.add(territory);
        player.sendMessage(String.format("§aO jogador §f%s §afoi banido do terreno.", targetName));
    }

    // TODO: Decide weather to allow offline players or not
    private static boolean handleTerritoryInput(Territory territory, Player player, String targetName) {
        OfflinePlayer member = Bukkit.getOfflinePlayer(targetName);

        if (member == null) {
            player.sendMessage("§cJogador não encontrado.");
            return false;
        }

        if (territory == null) {
            player.sendMessage("§cOcorreu um erro interno. Contate a nossa equipe.");
            return false;
        }

        return true;
    }
}
