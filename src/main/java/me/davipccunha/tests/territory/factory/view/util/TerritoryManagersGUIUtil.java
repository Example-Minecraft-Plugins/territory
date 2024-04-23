package me.davipccunha.tests.territory.factory.view.util;

import me.davipccunha.utils.inventory.InteractiveInventory;
import me.davipccunha.utils.item.CustomHead;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TerritoryManagersGUIUtil {
    public static int getNextSlot(int elements) {
        // This formula excludes the inventory borders
        return elements + 10 + (2 * Math.floorDiv(elements, 7));
    }

    public static ItemStack getInteractiveMemberSkull(String name, String location, String action, List<String> lore) {
        ItemStack playerHead = CustomHead.getPlayerHead(name);

        final Map<String, String> nbtTags = Map.of(
            "action", action,
            "member", name,
            "location", location
        );

        return InteractiveInventory.createActionItem(playerHead, nbtTags, "§r§e" + name, lore);
    }

    public static ItemStack getAddElementItem(String name, String location, String action, List<String> lore) {
        ItemStack addElement = CustomHead.getCustomHead("null",
                "http://textures.minecraft.net/texture/47a0fc6dcf739c11fece43cdd184dea791cf757bf7bd91536fdbc96fa47acfb",
                UUID.randomUUID());

        final HashMap<String, String> nbtTags = new HashMap<>(){{
            put("action", action);
            put("location", location);
        }};

        return InteractiveInventory.createActionItem(addElement, nbtTags, name, lore);
    }
}
