package org.lushplugins.regrowthtp.request;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.libraries.chatcolor.ModernChatColorHandler;
import org.lushplugins.regrowthtp.RegrowthTP;

import java.util.function.BiConsumer;

public enum TeleportDirection {
    // TODO: Create safe teleport method to avoid lava and drops of over 3 blocks
    TO((sender, receiver) -> {
        sender.teleport(receiver);

        sender.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", receiver.getName())));
        receiver.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("summoned-player")
            .replace("%player%", sender.getName())));
    }),
    SUMMON((sender, receiver) -> {
        receiver.teleport(sender);

        receiver.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", sender.getName())));
        sender.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("summoned-player")
            .replace("%player%", receiver.getName())));
    });

    private final BiConsumer<Player, Player> teleportFunction;

    TeleportDirection(BiConsumer<Player, Player> teleportFunction) {
        this.teleportFunction = teleportFunction;
    }

    public void apply(Player sender, Player receiver) {
        teleportFunction.accept(sender, receiver);
    }
}
