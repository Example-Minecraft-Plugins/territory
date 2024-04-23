package me.davipccunha.tests.territory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.factory.config.TerritoryMemberConfigFactory;
import me.davipccunha.tests.territory.util.TerritoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Getter
@Setter
@AllArgsConstructor
public class Territory {
    private final TerritoryBlock center;
    private TerritoryArea territoryArea;
    private String owner;
    private TerritoryConfig territoryConfig;

    public int getSide() {
        return this.territoryArea.getMaxX() - this.territoryArea.getMinX() + 1;
    }

    public void showBorders(TerritoryPlugin plugin, Player player) {
        try {
            player = player != null ? player : Bukkit.getPlayer(owner);

            TerritoryUtil.sendTerritoryBorderPacket(this, player);
            final Player finalPlayer = player;

            Bukkit.getScheduler().runTaskLater(plugin, () -> TerritoryUtil.sendDefaultWorldBorderPacket(finalPlayer), 20L * 20);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().warning("Failed to send world border packet to player " + player.getName());
        }
    }

    public void showBorders(TerritoryPlugin plugin) {
        this.showBorders(plugin, null);
    }

    public void addMember(String name) {
        this.territoryConfig.addMember(name);
    }

    public void removeMember(String name) {
        this.territoryConfig.removeMember(name);
    }

    @Override
    public String toString() {
        return String.format("T(%d, %d)", this.center.getX(), this.center.getZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Territory)) return false;
        Territory other = (Territory) obj;
        return this.center.equals(other.center);
    }

    @Override
    public int hashCode() {
        return this.center.hashCode();
    }
}
