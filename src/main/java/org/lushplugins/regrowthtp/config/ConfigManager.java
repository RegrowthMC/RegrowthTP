package org.lushplugins.regrowthtp.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.regrowthtp.RegrowthTP;

import java.util.Map;

public class ConfigManager {
    private long requestExpiryTime;
    private Map<String, String> messages;

    public ConfigManager() {
        RegrowthTP.getInstance().saveDefaultConfig();
    }

    public void reload() {
        RegrowthTP.getInstance().reloadConfig();
        FileConfiguration config = RegrowthTP.getInstance().getConfig();

        this.requestExpiryTime = config.getLong("request-expiry-time") * 1000L;
        this.messages = YamlUtils.getMap(config, "messages", String.class);
    }

    public long getRequestExpiryTime() {
        return requestExpiryTime;
    }

    public String getMessage(String id) {
        return messages.getOrDefault(id, "");
    }
}
