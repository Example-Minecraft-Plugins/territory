package me.davipccunha.tests.territory.factory.view;

import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.utils.inventory.InteractiveInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerritoryConfigGUI {
    public static Inventory createTerritoryConfigGUI(Territory territory) {
        final String territoryCenter = territory.getCenter().serialize();

        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "Gerenciar Terreno");

        ItemStack sign = new ItemStack(Material.SIGN);
        ItemMeta signMeta = sign.getItemMeta();
        signMeta.setDisplayName("§r§eInformações do Terreno");

        final List<String> territoryInfoLore = Arrays.asList(
                String.format("§7 * Dono: §f%s", territory.getOwner()),
                String.format("§7 * Tamanho: §f%dx%d", territory.getSide(), territory.getSide()),
                String.format("§7 * Membros: §f%d", territory.getTerritoryConfig().getMembersCount() - 1),
                String.format("§7 * Banidos: §f%d", territory.getTerritoryConfig().getBannedPlayers().size())
        );

        signMeta.setLore(territoryInfoLore);
        sign.setItemMeta(signMeta);

        final Map<String, String> accessibleTags = Map.of(
            "action", "toggleAccessible",
            "location", territoryCenter
        );

        final ItemStack accessible = InteractiveInventory.createToggleItem(
                territory.getTerritoryConfig().isAccessible(),
                accessibleTags,
                "§r§eLivre acesso",
                "§f * Permite a entrada de jogadores não adicionados");


        final Map<String, String> pvpTags = Map.of(
            "action", "togglePvp",
            "location", territoryCenter
        );

        final ItemStack pvp = InteractiveInventory.createToggleItem(
                territory.getTerritoryConfig().isPvpEnabled(),
                pvpTags,
                "§r§ePvP",
                "§f * Permite que jogadores se ataquem no terreno");


        final Map<String, String> membersTags = Map.of(
            "action", "members",
            "location", territoryCenter
        );

        final List<String> membersLore = List.of(
                "§7  Clique para gerenciar os membros do terreno"
        );

        ItemStack members = InteractiveInventory.createActionItem(
                new ItemStack(Material.SKULL_ITEM, 1, (short) 3),
                membersTags,
                "§r§eGerenciar membros",
                membersLore);

        inventory.setItem(4, sign);
        inventory.setItem(20, accessible);
        inventory.setItem(22, pvp);
        inventory.setItem(24, members);

        return inventory;
    }
}
