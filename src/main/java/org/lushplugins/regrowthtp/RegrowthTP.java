package org.lushplugins.regrowthtp;

import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.regrowthtp.command.TPCommand;
import org.lushplugins.regrowthtp.config.ConfigManager;
import org.lushplugins.regrowthtp.listener.PlayerListener;
import org.lushplugins.regrowthtp.request.RequestManager;
import org.lushplugins.regrowthtp.storage.StorageManager;
import org.lushplugins.regrowthtp.user.UserCache;
import revxrsal.commands.bukkit.BukkitLamp;

public final class RegrowthTP extends SpigotPlugin {
    private static RegrowthTP plugin;

    private ConfigManager configManager;
    private RequestManager requestManager;
    private UserCache userCache;
    private StorageManager storageManager;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager();
        this.configManager.reload();
        this.requestManager = new RequestManager();
        this.userCache = new UserCache(this);
        this.storageManager = new StorageManager();
        this.storageManager.reload();

        registerListener(new PlayerListener());

        BukkitLamp.builder(this)
            .build()
            .register(new TPCommand());
    }

    @Override
    public void onDisable() {
        if (storageManager != null) {
            storageManager.shutdown();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public static RegrowthTP getInstance() {
        return plugin;
    }
}
