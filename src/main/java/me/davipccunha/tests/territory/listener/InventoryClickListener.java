package me.davipccunha.tests.territory.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.cache.TerritoryCache;
import me.davipccunha.tests.territory.factory.view.TerritoryBansManagerGUI;
import me.davipccunha.tests.territory.factory.view.TerritoryConfigGUI;
import me.davipccunha.tests.territory.factory.view.TerritoryMemberConfigGUI;
import me.davipccunha.tests.territory.factory.view.TerritoryMemberManagerGUI;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryBlock;
import me.davipccunha.tests.territory.model.TerritoryInput;
import me.davipccunha.utils.item.NBTHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class InventoryClickListener implements Listener {
    private final TerritoryPlugin plugin;

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory == null) return;
        if (event.getCurrentItem() == null) return;

        String inventoryName = inventory.getName();
        if (!inventoryName.equals("Gerenciar Terreno")
                && !inventoryName.equals("Gerenciar Membros")
                && !inventoryName.contains("Permissões de ")
                && !inventoryName.equals("Gerenciar Banimentos")
                && !inventoryName.equals("Seus Terrenos"))
            return;

        event.setCancelled(true);

        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        final Player player = (Player) event.getWhoClicked();

        if (player == null) return;

        ItemStack clickedItem = event.getCurrentItem();
        String action = NBTHandler.getNBT(clickedItem, "action");
        String territoryLocation = NBTHandler.getNBT(clickedItem, "location");

        if (action == null || territoryLocation == null) return;

        TerritoryCache cache = plugin.getTerritoryCache();

        TerritoryBlock center = TerritoryBlock.deserialize(territoryLocation);
        Territory territory = cache.getTerritory(center.getX(), center.getZ());

        if (territory == null) return;

        if (!territory.getOwner().equals(player.getName())) {
            player.sendMessage("§cApenas o dono do terreno pode alterar suas configurações.");
            return;
        }

        final String memberName = NBTHandler.getNBT(clickedItem, "member");

        switch (action) {
            // Configuração do Terreno
            case "toggleAccessible":
                boolean currentAccessible = territory.getTerritoryConfig().isAccessible();
                territory.getTerritoryConfig().setAccessible(!currentAccessible);
                cache.add(territory);

                player.sendMessage("§aLivre acesso " + (currentAccessible ? "§cdesativado" : "ativado") + " §acom sucesso.");
                player.openInventory(TerritoryConfigGUI.createTerritoryConfigGUI(territory));

                break;
            case "togglePvp":
                boolean currentPvp = territory.getTerritoryConfig().isPvpEnabled();
                territory.getTerritoryConfig().setPvpEnabled(!currentPvp);
                cache.add(territory);

                player.sendMessage("§aPvP " + (currentPvp ? "§cdesativado" : "ativado") + " §acom sucesso.");
                player.openInventory(TerritoryConfigGUI.createTerritoryConfigGUI(territory));

                break;

            case "members":
                Inventory membersManagerGUI = TerritoryMemberManagerGUI.createTerritoryMemberConfigGUI(territory);
                player.openInventory(membersManagerGUI);

                break;

            // Gerenciar Membros
            case "addMember":
                player.sendMessage("§aDigite o nome do jogador que deseja adicionar ao terreno no chat.");
                plugin.getAwaitingTerritoryInput().put(player.getName(), new TerritoryInput("addMember", center));

                player.closeInventory();

                break;

            case "manageMember":
                player.openInventory(TerritoryMemberConfigGUI.createTerritoryMemberConfigGUI(territory, memberName));

                break;

            case "manageBans":
                player.openInventory(TerritoryBansManagerGUI.createTerritoryBansManagerGUI(territory));

                break;

            // Permissões de Membro
            case "toggleCanBuild":
                boolean currentCanBuild = territory.getTerritoryConfig().getMemberConfig(memberName).canBuild();
                territory.getTerritoryConfig().getMemberConfig(memberName).setCanBuild(!currentCanBuild);
                cache.add(territory);

                player.sendMessage(String.format("§aPermissão de construir para §f%s §a%s §acom sucesso.", memberName, currentCanBuild ? "§cdesativada" : "ativada"));

                player.openInventory(TerritoryMemberConfigGUI.createTerritoryMemberConfigGUI(territory, memberName));

                break;

            case "toggleCanDamageEntities":
                boolean currentCanDamageEntities = territory.getTerritoryConfig().getMemberConfig(memberName).canDamageEntities();
                territory.getTerritoryConfig().getMemberConfig(memberName).setCanDamageEntities(!currentCanDamageEntities);
                cache.add(territory);

                player.sendMessage(String.format("§aPermissão de atacar entidades para §f%s §a%s §acom sucesso.", memberName, currentCanDamageEntities ? "§cdesativada" : "ativada"));

                player.openInventory(TerritoryMemberConfigGUI.createTerritoryMemberConfigGUI(territory, memberName));

                break;

            case "toggleCanInteract":
                boolean currentCanInteract = territory.getTerritoryConfig().getMemberConfig(memberName).canInteract();
                territory.getTerritoryConfig().getMemberConfig(memberName).setCanInteract(!currentCanInteract);
                cache.add(territory);

                player.sendMessage(String.format("§aPermissão de interagir para §f%s §a%s §acom sucesso.", memberName, currentCanInteract ? "§cdesativada" : "ativada"));
                player.openInventory(TerritoryMemberConfigGUI.createTerritoryMemberConfigGUI(territory, memberName));

                break;

            case "removeMember":
                territory.removeMember(memberName);
                cache.add(territory);

                player.sendMessage(String.format("§aVocê removeu §f%s §ado terreno.", memberName));
                player.openInventory(TerritoryMemberManagerGUI.createTerritoryMemberConfigGUI(territory));

                break;

            // Gerenciar Banimentos
            case "banPlayer":
                player.sendMessage("§aDigite o nome do jogador que deseja banir do terreno no chat.");
                plugin.getAwaitingTerritoryInput().put(player.getName(), new TerritoryInput("banPlayer", center));

                player.closeInventory();

                break;

            case "unbanPlayer":
                String bannedPlayer = NBTHandler.getNBT(clickedItem, "member");
                territory.getTerritoryConfig().unbanPlayer(bannedPlayer);
                cache.add(territory);

                player.sendMessage(String.format("§aVocê desbaniu §f%s §ado terreno.", bannedPlayer));
                player.openInventory(TerritoryBansManagerGUI.createTerritoryBansManagerGUI(territory));

                break;

            // Seus Terrenos
            case "teleport":
                player.teleport(center.toLocation(player.getLocation().getPitch(), player.getLocation().getYaw()).add(0, 1, 0));
                player.sendMessage(String.format("§aVocê foi teleportado para o terreno %s.", center));

                break;
        }
    }
}
