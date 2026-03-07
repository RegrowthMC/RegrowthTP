package org.lushplugins.regrowthtp;

import org.jooq.impl.SQLDataType;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.regrowthtp.command.TPCommand;
import org.lushplugins.regrowthtp.config.ConfigManager;
import org.lushplugins.regrowthtp.listener.PlayerListener;
import org.lushplugins.regrowthtp.request.RequestManager;
import org.lushplugins.regrowthtp.user.UserCache;
import org.lushplugins.storagehandler.StorageHandler;
import revxrsal.commands.bukkit.BukkitLamp;

public final class RegrowthTP extends SpigotPlugin {
    private static RegrowthTP plugin;

    private ConfigManager configManager;
    private RequestManager requestManager;
    private UserCache userCache;
    private StorageHandler storageHandler;

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
        this.storageHandler = StorageHandler.builder(this).build();
        this.storageHandler.execute(context -> context
            .createTableIfNotExists("regrowthtp_users")
            .column("uuid", SQLDataType.UUID.notNull())
            .column("requests_enabled", SQLDataType.BOOLEAN)
            .primaryKey("uuid")
            .execute()
        );

        registerListener(new PlayerListener());

        BukkitLamp.builder(this)
            .build()
            .register(new TPCommand());
    }

    @Override
    public void onDisable() {
        if (storageHandler != null) {
            storageHandler.shutdown();
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

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public static RegrowthTP getInstance() {
        return plugin;
    }
}
