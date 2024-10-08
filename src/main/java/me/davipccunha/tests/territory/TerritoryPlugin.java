package me.davipccunha.tests.territory;

import lombok.Getter;
import me.davipccunha.tests.territory.cache.TerritoryCache;
import me.davipccunha.tests.territory.command.TerrenoCommand;
import me.davipccunha.tests.territory.command.TerrenosCommand;
import me.davipccunha.tests.territory.listener.*;
import me.davipccunha.tests.territory.model.Territory;
import me.davipccunha.tests.territory.model.TerritoryInput;
import me.davipccunha.tests.territory.model.TerritoryUser;
import me.davipccunha.utils.cache.RedisCache;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Getter
public class TerritoryPlugin extends JavaPlugin {
    private TerritoryCache territoryCache;
    private RedisCache<Territory> territoryRedisCache;
    private RedisCache<TerritoryUser> userRedisCache;

    // A TerritoryInput is a class that contains the action the input refers to and the location of the territory
    // TODO: Input may be taken using an AnvilInventory by getting the displayName of the resulting item
    private final HashMap<String, TerritoryInput> awaitingTerritoryInput = new HashMap<>();

    @Override
    public void onEnable() {
        this.init();
        getLogger().info("Territory plugin loaded!");
    }

    public void onDisable() {
        getLogger().info("Territory plugin unloaded!");
    }

    private void init() {
        saveDefaultConfig();
        this.registerListeners(
                new BuildListener(this),
                new EntityDamageEntityListener(this),
                new PlayerInteractListener(this),
                new InventoryClickListener(this),
                new AsyncPlayerChatListener(this),
                new PlayerMoveListener(this)
        );
        this.registerCommands();

        this.loadCaches();

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
            getLogger().warning("ProtocolLib not found. Some features may not work properly.");
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) pluginManager.registerEvents(listener, this);
    }

    private void registerCommands() {
        getCommand("terreno").setExecutor(new TerrenoCommand(this));
        getCommand("terrenos").setExecutor(new TerrenosCommand(this));
    }

    private void loadCaches() {
        this.territoryCache = new TerritoryCache(this);
        this.territoryRedisCache = new RedisCache<>("territories:territories", Territory.class);
        this.userRedisCache = new RedisCache<>("territories:users", TerritoryUser.class);

        territoryCache.load();
    }
}
