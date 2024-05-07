package me.davipccunha.tests.territory.command.subcommand;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.territory.TerritoryPlugin;
import me.davipccunha.tests.territory.factory.config.TerritoryConfigFactory;
import me.davipccunha.tests.territory.model.*;
import me.davipccunha.utils.cache.RedisCache;
import me.davipccunha.utils.permission.PermissionUtils;
import me.davipccunha.utils.world.WorldUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

@RequiredArgsConstructor
public class AdquirirSubCommand implements TerrenoSubCommand {
    private final TerritoryPlugin plugin;

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) return false;

        int size = NumberUtils.toInt(args[1]);
        if (size <= 0) {
            player.sendMessage("§cTamanho de terreno inválido.");
            return true;
        }

        final int minSize = plugin.getConfig().getInt("territories.min-size");
        final int maxSize = plugin.getConfig().getInt("territories.max-size");

        if (minSize < 1 || maxSize < 1) throw new IllegalArgumentException("O tamanho mínimo e máximo do terreno devem ser maiores que 0.");
        if (minSize > maxSize) throw new IllegalArgumentException("O tamanho mínimo do terreno não pode ser maior que o tamanho máximo.");

        if (size < minSize || size > maxSize) {
            player.sendMessage(String.format("§cO tamanho do terreno deve ser maior que %s e menor que %s.", minSize, maxSize));
            return true;
        }

        final Location location = player.getLocation();
        final String name = player.getName();

        final TerritoryConfig defaultConfig = TerritoryConfigFactory.createDefault(name);
        final TerritoryBlock center = new TerritoryBlock(location);
        final Territory territory = new Territory(center, new TerritoryArea(location, size), name, defaultConfig);

        if (isTerritoryOverlapping(territory, minSize)) {
            player.sendMessage("§cO terreno não pode sobrepor outro terreno.");
            return true;
        }

        final RedisCache<TerritoryUser> cache = plugin.getUserRedisCache();

        TerritoryUser territoryUser = cache.get(name);
        if (territoryUser == null) {
            territoryUser = new TerritoryUser(player.getName());
            cache.add(player.getName(), territoryUser);
        }

        Optional<PermissionAttachmentInfo> maxLimitPermission = player.getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith("territory.limit."))
                .max(Comparator.comparingInt(permission -> NumberUtils.toInt(permission.getPermission().split("\\.")[2])));

        final int defaultMaxLimit = plugin.getConfig().getInt("territories.default-limit");

        final int maxLimit = maxLimitPermission.map(permissionAttachmentInfo ->
                PermissionUtils.extractNumberSuffix(permissionAttachmentInfo.getPermission())).orElse(defaultMaxLimit);

        if (territoryUser.getTerritoriesAmount() >= maxLimit && !player.hasPermission("territory.admin")) {
            player.sendMessage("§cVocê já atingiu seu limite de homes.");
            return true;
        }

        plugin.getTerritoryCache().add(territory);
        this.showTerritoryBorders(territory);

        territoryUser.addTerritory(territory.getCenter());
        cache.add(name, territoryUser);

        player.sendMessage(String.format("§aTerreno de tamanho %dx%d adquirido com sucesso.", territory.getSide(), territory.getSide()));
        return true;
    }

    @Override
    public String getUsage() {
        return "/terreno adquirir <tamanho>";
    }

    private void showTerritoryBorders(Territory territory) {
        territory.showBorders(plugin);
    }

    private boolean isTerritoryOverlapping(Territory territory, int minSize) {
        final String worldName = territory.getTerritoryArea().getWorldName();
        final int minX = territory.getTerritoryArea().getMinX();
        final int maxX = territory.getTerritoryArea().getMaxX();
        final int minZ = territory.getTerritoryArea().getMinZ();
        final int maxZ = territory.getTerritoryArea().getMaxZ();

        List<TerritoryBlock> blocksToCheck = new ArrayList<>(territory.getTerritoryArea().getCorners());

        blocksToCheck.add(new TerritoryBlock(worldName, territory.getCenter().getX(), territory.getCenter().getZ()));

        for (int x = minX; x <= maxX + 16; x+=16) {
            for (int z = minZ; z <= maxZ + 16; z+=16) {
                blocksToCheck.add(new TerritoryBlock(worldName, x, z));
            }
        }

        for (TerritoryBlock block : blocksToCheck) {
            final long hash = WorldUtils.chunkHash(block.getX(), block.getZ());
            final Set<Territory> chunkTerritories = plugin.getTerritoryCache().getTerritories(hash);
            if (chunkTerritories == null) continue;

            for (Territory chunkTerritory : chunkTerritories) {
                final int territoryMinX = territory.getTerritoryArea().getMinX();
                final int territoryMaxX = territory.getTerritoryArea().getMaxX();
                final int territoryMinZ = territory.getTerritoryArea().getMinZ();
                final int territoryMaxZ = territory.getTerritoryArea().getMaxZ();

                for (int x = territoryMinX; x <= territoryMaxX + minSize; x+=minSize) {
                    for (int z = territoryMinZ; z <= territoryMaxZ + minSize; z+=minSize) {
                        if (chunkTerritory.getTerritoryArea().contains(x, z)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
