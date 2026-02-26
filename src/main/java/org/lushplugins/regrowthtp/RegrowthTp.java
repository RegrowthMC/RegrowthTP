package org.lushplugins.regrowthtp;

import org.bukkit.plugin.java.JavaPlugin;

public final class RegrowthTp extends JavaPlugin {
    private static RegrowthTp plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Enable implementation
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public static RegrowthTp getInstance() {
        return plugin;
    }
}
