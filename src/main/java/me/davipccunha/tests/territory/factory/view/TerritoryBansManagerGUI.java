package me.davipccunha.tests.territory.factory.view;

import me.davipccunha.tests.territory.factory.view.util.TerritoryManagersGUIUtil;
import me.davipccunha.tests.territory.model.Territory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TerritoryBansManagerGUI {
    public static Inventory createTerritoryBansManagerGUI(Territory territory) {
        Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Gerenciar Banimentos");

        String territoryCenter = territory.getCenter().serialize();

        final List<String> playerHeadLore = List.of(
                "§7  Clique para desbanir este jogador"
        );

        int i = 0;
        for (String banned : territory.getTerritoryConfig().getBannedPlayers()) {
            if (i >= 28) break; // Might add multiple pages but limiting to 28 members for now

            final ItemStack playerHead = TerritoryManagersGUIUtil.getInteractiveMemberSkull(banned, territoryCenter, "unbanPlayer", playerHeadLore);
            inventory.setItem(TerritoryManagersGUIUtil.getNextSlot(i), playerHead);
            i++;
        }

        final int lastSlot = TerritoryManagersGUIUtil.getNextSlot(territory.getTerritoryConfig().getBannedPlayers().size());

        final List<String> banLore = List.of(
                "§7  Clique para banir um jogador do terreno"
        );

        if (10 <= lastSlot && lastSlot <= 43) {
            ItemStack addMember = TerritoryManagersGUIUtil.getAddElementItem("§aBanir Jogador", territoryCenter, "banPlayer", banLore);
            inventory.setItem(lastSlot, addMember);
        }

        return inventory;
    }
}
