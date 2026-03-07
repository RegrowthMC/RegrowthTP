package org.lushplugins.regrowthtp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.lushplugins.lushlib.libraries.chatcolor.ModernChatColorHandler;
import org.lushplugins.regrowthtp.RegrowthTP;
import org.lushplugins.regrowthtp.request.RequestManager;
import org.lushplugins.regrowthtp.request.TeleportDirection;
import org.lushplugins.regrowthtp.request.TeleportRequest;
import org.lushplugins.regrowthtp.user.TPUser;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@SuppressWarnings("unused")
@Command("tp")
public class TPCommand {

    // TODO: Support ignoring commas in location for easier coordinate copy-pasting
    @Command({"tp location", "tploc"})
    @CommandPermission(value = "tp.location")
    public void location(
        BukkitCommandActor actor,
        Location location,
        @Optional World world,
        @Optional Player target
    ) {
        if (world != null) {
            location.setWorld(world);
        }

        target = target != null ? target : actor.requirePlayer();
        location.setYaw(target.getYaw());
        location.setPitch(target.getPitch());

        target.teleportAsync(location);

        target.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", "%s, %s, %s".formatted(location.getX(), location.getY(), location.getZ()))));
    }

    @Command("tp to")
    @CommandPermission(value = "tp.to")
    public void to(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        player.teleport(target);

        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", target.getName())));
    }

    @Command("tp summon")
    @CommandPermission(value = "tp.summon")
    public void summon(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        target.teleport(player);

        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("summoned-player")
            .replace("%player%", target.getName())));
    }

    @Command({"tp request", "tpr"})
    @CommandPermission(value = "tp.request", defaultAccess = PermissionDefault.TRUE)
    public void request(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        TPUser targetUser = RegrowthTP.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (targetUser != null && !targetUser.areRequestsEnabled()) {
            player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("requests-disabled")
                .replace("%player%", target.getName())));
            return;
        }

        RegrowthTP.getInstance().getRequestManager().sendRequest(new TeleportRequest(
            player.getUniqueId(),
            target.getUniqueId(),
            TeleportDirection.TO
        ));

        target.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("received-teleport-request")
            .replace("%player%", player.getName())));
        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("sent-request")
            .replace("%player%", target.getName())));
    }

    @Command({"tp invite", "tpi"})
    @CommandPermission(value = "tp.invite", defaultAccess = PermissionDefault.TRUE)
    public void invite(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        TPUser targetUser = RegrowthTP.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (targetUser != null && !targetUser.areRequestsEnabled()) {
            player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("requests-disabled")
                .replace("%player%", target.getName())));
            return;
        }

        RegrowthTP.getInstance().getRequestManager().sendRequest(new TeleportRequest(
            player.getUniqueId(),
            target.getUniqueId(),
            TeleportDirection.SUMMON
        ));

        target.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("received-summon-request")
            .replace("%player%", player.getName())));
        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("sent-request")
            .replace("%player%", target.getName())));
    }

    @Command({"tp accept", "tpyes"})
    @CommandPermission(value = "tp.accept", defaultAccess = PermissionDefault.TRUE)
    public void accept(BukkitCommandActor actor, @Optional Player target) {
        Player player = actor.requirePlayer();
        RequestManager requestManager = RegrowthTP.getInstance().getRequestManager();
        TeleportRequest request;
        if (target != null) {
            request = requestManager.findRequest(target.getUniqueId(), player.getUniqueId());
        } else {
            request = requestManager.findMostRecentRequest(player.getUniqueId());
        }

        if (request == null) {
            if (target == null) {
                player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("no-requests")));
            } else {
                player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("no-request")
                    .replace("%player%", target.getName())));
            }
            return;
        }

        if (target == null) {
            target = Bukkit.getPlayer(request.from());
        }

        request.direction().apply(target, player);
        requestManager.removeRequest(request);
    }

    @Command({"tp deny", "tpno"})
    @CommandPermission(value = "tp.deny", defaultAccess = PermissionDefault.TRUE)
    public void deny(BukkitCommandActor actor, @Optional Player target) {
        Player player = actor.requirePlayer();
        RequestManager requestManager = RegrowthTP.getInstance().getRequestManager();
        TeleportRequest request;
        if (target != null) {
            request = requestManager.findRequest(target.getUniqueId(), player.getUniqueId());
        } else {
            request = requestManager.findMostRecentRequest(player.getUniqueId());
        }

        if (request == null) {
            if (target == null) {
                player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("no-requests")));
            } else {
                player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("no-request")
                    .replace("%player%", target.getName())));
            }
            return;
        }

        if (target == null) {
            target = Bukkit.getPlayer(request.from());
        }

        requestManager.removeRequest(request);
        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("denied-request")
            .replace("%player%", target.getName())));
    }

    @Command("tp toggle")
    @CommandPermission(value = "tp.toggle", defaultAccess = PermissionDefault.TRUE)
    public void toggle(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        TPUser user = RegrowthTP.getInstance().getUserCache().getCachedUser(player.getUniqueId());
        if (user == null) {
            return;
        }

        user.setRequestsEnabled(!user.areRequestsEnabled());

        player.sendMessage(ModernChatColorHandler.translate(RegrowthTP.getInstance().getConfigManager().getMessage("toggle-requests")
            .replace("%status%", user.areRequestsEnabled() ? "enabled" : "disabled")));
    }

    @Subcommand("reload")
    @CommandPermission("tp.reload")
    public void reload(CommandSender sender) {
        RegrowthTP.getInstance().getConfigManager().reload();

        sender.sendMessage(Component.text()
            .content("RegrowthTP reloaded!")
            .color(TextColor.fromHexString("#b7faa2"))
            .build());
    }
}
