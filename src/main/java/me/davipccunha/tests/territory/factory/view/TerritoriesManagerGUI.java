package me.davipccunha.tests.territory.factory.view;

import me.davipccunha.tests.territory.factory.view.util.TerritoryManagersGUIUtil;
import me.davipccunha.tests.territory.model.TerritoryBlock;
import me.davipccunha.tests.territory.model.TerritoryUser;
import me.davipccunha.utils.inventory.InteractiveInventory;
import me.davipccunha.utils.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class TerritoriesManagerGUI {
    public static Inventory createUserTerritoriesGUI(TerritoryUser territoryUser) {
        if (territoryUser.getTerritories().isEmpty())
            return createEmptyTerritoriesGUI();

        Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Seus Terrenos");

        int i = 1;
        for (TerritoryBlock territoryCenter : territoryUser.getTerritories()) {
            final Map<String, String> tags = Map.of(
                "action", "teleport",
                "location", territoryCenter.serialize()
            );

            final List<String> lore = List.of(
                "§7  Clique para se teleportar para este terreno"
            );

            final ItemStack icon = InteractiveInventory.createActionItem(getTerritoryIcon(territoryCenter), tags, "§r§e" + territoryCenter, lore);
            final ItemStack defaultIcon = InteractiveInventory.createActionItem(new ItemStack(Material.GRASS), tags, "§r§e" + territoryCenter, lore);

            // Some blocks don't have an item representation, such as water, lava, air, etc.
            final ItemStack finalIcon = icon == null ? defaultIcon : icon;

            finalIcon.setAmount(i);
            final int slot = TerritoryManagersGUIUtil.getNextSlot(i - 1);

            inventory.setItem(slot, finalIcon);
            i++;
        }

        return inventory;
    }

    public static Inventory createEmptyTerritoriesGUI() {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "Seus Terrenos");

        final ItemStack empty = new ItemStack(Material.BARRIER);
        final ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setDisplayName("§r§eVocê não possui nenhum terreno");
        empty.setItemMeta(emptyMeta);

        inventory.setItem(13, empty);

        return inventory;
    }

    private static ItemStack getTerritoryIcon(TerritoryBlock center) {
        final World world = Bukkit.getWorld(center.getWorldName());
        final int x = center.getX();
        final int z = center.getZ();

        final Block block = world.getBlockAt(x, WorldUtils.getHighestY(center.getWorldName(), x, z), z);
        return new ItemStack(block.getType(), 1, block.getData());
    }
}
