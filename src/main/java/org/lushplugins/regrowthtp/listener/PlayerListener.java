package org.lushplugins.regrowthtp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.lushplugins.regrowthtp.RegrowthTP;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RegrowthTP.getInstance().getRequestManager().clearRequests(event.getPlayer().getUniqueId());
    }
}
