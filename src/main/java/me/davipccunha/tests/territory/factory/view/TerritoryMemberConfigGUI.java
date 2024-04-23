package me.davipccunha.tests.territory.factory.view;

import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryMemberConfig;
import me.davipccunha.utils.inventory.InteractiveInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class TerritoryMemberConfigGUI {
    public static Inventory createTerritoryMemberConfigGUI(Territory territory, String memberName) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Permissões de " + memberName);

        String territoryCenter = territory.getCenter().serialize();
        TerritoryMemberConfig memberConfig = territory.getTerritoryConfig().getMemberConfig(memberName);

        final HashMap<String, String> canBuildTags = new HashMap<>() {{
            put("action", "toggleCanBuild");
            put("member", memberName);
            put("location", territoryCenter);
        }};

        final ItemStack canBuild = InteractiveInventory.createToggleItem(
                memberConfig.canBuild(),
                canBuildTags,
                "§r§eConstruir",
                "§f * Permite que o jogador construa no terreno");

        final HashMap<String, String> canDamageEntitiesTags = new HashMap<>() {{
            put("action", "toggleCanDamageEntities");
            put("member", memberName);
            put("location", territoryCenter);
        }};

        final ItemStack canDamageEntities = InteractiveInventory.createToggleItem(
                memberConfig.canDamageEntities(),
                canDamageEntitiesTags,
                "§r§eAtacar Entidades",
                "§f * Permite que o jogador ataque entidades no terreno");

        final HashMap<String, String> canInteractTags = new HashMap<>() {{
            put("action", "toggleCanInteract");
            put("member", memberName);
            put("location", territoryCenter);
        }};

        final ItemStack canInteract = InteractiveInventory.createToggleItem(
                memberConfig.canInteract(),
                canInteractTags,
                "§r§eInteragir",
                "§f * Permite que o jogador abra baús e portas no terreno");

        final HashMap<String, String> removeMemberTags = new HashMap<>() {{
            put("action", "removeMember");
            put("member", memberName);
            put("location", territoryCenter);
        }};

        final List<String> removeMemberLore = List.of(
            "§7  Clique para remover o membro do terreno"
        );

        final ItemStack removeMember = InteractiveInventory.createActionItem(
                new ItemStack(Material.BARRIER, 1),
                removeMemberTags, "§r§cRemover Membro", removeMemberLore);

        inventory.setItem(11, canBuild);
        inventory.setItem(13, canDamageEntities);
        inventory.setItem(15, canInteract);
        inventory.setItem(26, removeMember);

        return inventory;
    }
}
