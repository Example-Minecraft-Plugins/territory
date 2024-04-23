package me.davipccunha.tests.territory.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryArea;
import me.davipccunha.tests.territory.model.TerritoryBlock;
import me.davipccunha.utils.world.WorldUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;

public class TerritoryUtil {
    public static Collection<Long> getTerritoryChunksHashes(TerritoryArea area) {
        Collection<Long> hashes = new HashSet<>();
        for (TerritoryBlock corner : area.getCorners()) {
            hashes.add(WorldUtils.chunkHash(corner.getX(), corner.getZ()));
        }

        for (int x = area.getMinX(); x <= area.getMaxX(); x+=16) {
            for (int z = area.getMinZ(); z <= area.getMaxZ(); z+=16) {
                hashes.add(WorldUtils.chunkHash(x, z));
            }
        }

        return hashes;
    }

    public static Territory getPreciseTerritory(Collection<Territory> chunkTerritories, int x, int z) {
        if (chunkTerritories == null) return null;
        for (Territory territory : chunkTerritories) {
            if (territory.getTerritoryArea().contains(x, z)) {
                return territory;
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static void sendTerritoryBorderPacket(Territory territory, Player player) throws InvocationTargetException {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        PacketContainer worldBorderPacket = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
        worldBorderPacket.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE);

        // The +0.5 is to make the border align with the real block border
        worldBorderPacket.getDoubles().write(0, (double) territory.getCenter().getX() + 0.5);
        worldBorderPacket.getDoubles().write(1, (double) territory.getCenter().getZ() + 0.5);
        worldBorderPacket.getDoubles().write(2, (double) territory.getSide());
        worldBorderPacket.getDoubles().write(3, (double) territory.getSide());
        worldBorderPacket.getLongs().write(0, 0L);
        worldBorderPacket.getIntegers().write(0, 29999984);
        worldBorderPacket.getIntegers().write(1, 0);
        worldBorderPacket.getIntegers().write(2, 0);

        protocolManager.sendServerPacket(player, worldBorderPacket);
    }

    @SuppressWarnings("deprecation")
    public static void sendDefaultWorldBorderPacket(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        PacketContainer worldBorderPacket = new PacketContainer(PacketType.Play.Server.WORLD_BORDER);
        worldBorderPacket.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.INITIALIZE);
        worldBorderPacket.getDoubles().write(0, 0.0);
        worldBorderPacket.getDoubles().write(1, 0.0);
        worldBorderPacket.getDoubles().write(2, 30000.0);
        worldBorderPacket.getDoubles().write(3, 30000.0);
        worldBorderPacket.getLongs().write(0, 0L);
        worldBorderPacket.getIntegers().write(0, 29999984);
        worldBorderPacket.getIntegers().write(1, 0);
        worldBorderPacket.getIntegers().write(2, 0);

        protocolManager.sendServerPacket(player, worldBorderPacket);
    }
}
