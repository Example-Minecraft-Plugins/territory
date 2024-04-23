package me.davipccunha.tests.territory.factory.view;

import me.davipccunha.tests.territory.factory.view.util.TerritoryManagersGUIUtil;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.utils.inventory.InteractiveInventory;
import me.davipccunha.utils.item.CustomHead;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TerritoryMemberManagerGUI {
    public static Inventory createTerritoryMemberConfigGUI(Territory territory) {
        Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Gerenciar Membros");

        String territoryCenter = territory.getCenter().serialize();

        final List<String> playerHeadLore = List.of(
                "§7  Clique para gerenciar as permissões do membro"
        );

        int i = 0;
        for (String member : territory.getTerritoryConfig().getMembers()) {
            if (i >= 28) break; // Might add multiple pages but limiting to 28 members for now
            if (member.equals(territory.getOwner())) continue;

            ItemStack playerHead = TerritoryManagersGUIUtil.getInteractiveMemberSkull(member, territoryCenter, "manageMember", playerHeadLore);
            inventory.setItem(TerritoryManagersGUIUtil.getNextSlot(i), playerHead);

            i++;
        }

        // We remove the owner from the members count
        final int lastSlot = TerritoryManagersGUIUtil.getNextSlot(territory.getTerritoryConfig().getMembersCount() - 1);

        final List<String> addLore = List.of(
                "§7  Clique para adicionar um membro ao terreno"
        );

        if (10 <= lastSlot && lastSlot <= 43) {
            ItemStack addMember = TerritoryManagersGUIUtil.getAddElementItem("§aAdicionar Membro", territoryCenter, "addMember", addLore);
            inventory.setItem(lastSlot, addMember);
        }

        inventory.setItem(53, getBansManagerItem(territory.getCenter().serialize()));

        return inventory;
    }

    private static ItemStack getBansManagerItem(String territoryCenter) {
        ItemStack bansManager = CustomHead.getCustomHead("null",
                "http://textures.minecraft.net/texture/afd2400002ad9fbbbd0066941eb5b1a384ab9b0e48a178ee96e4d129a5208654",
                UUID.randomUUID());

        final HashMap<String, String> nbtTags = new HashMap<>(){{
            put("action", "manageBans");
            put("location", territoryCenter);
        }};

        final List<String> lore = List.of(
                "§7  Clique para gerenciar os banimentos do terreno"
        );

        return InteractiveInventory.createActionItem(bansManager, nbtTags, "§aGerenciar Banimentos", lore);
    }
}
