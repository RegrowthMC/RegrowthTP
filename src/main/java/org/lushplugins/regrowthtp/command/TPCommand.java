package org.lushplugins.regrowthtp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.lushplugins.lushlib.libraries.chatcolor.paper.PaperColor;
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

import java.util.UUID;

@SuppressWarnings("unused")
@Command("tp")
public class TPCommand {

    // TODO: Support ignoring commas in location for easier coordinate copy-pasting
    @Command({"tp location", "tploc"})
    @CommandPermission("tp.location")
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

        PaperColor.handler().sendMessage(target, RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", "%s, %s, %s".formatted(location.getX(), location.getY(), location.getZ())));
    }

    @Command("tp to")
    @CommandPermission("tp.to")
    public void to(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        if (player == target) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("self-request"));
            return;
        }

        player.teleport(target);

        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("teleported-to")
            .replace("%target%", target.getName()));
    }

    @Command("tp summon")
    @CommandPermission("tp.summon")
    public void summon(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        if (player == target) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("self-request"));
            return;
        }

        target.teleport(player);

        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("summoned-player")
            .replace("%player%", target.getName()));
    }

    @Command({"tp request", "tpr", "tpa"})
    @CommandPermission(value = "tp.request", defaultAccess = PermissionDefault.TRUE)
    public void request(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        if (player == target) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("self-request"));
            return;
        }

        TPUser targetUser = RegrowthTP.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (targetUser != null && !targetUser.areRequestsEnabled()) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("requests-disabled")
                .replace("%player%", target.getName()));
            return;
        }

        UUID from = player.getUniqueId();
        UUID to = target.getUniqueId();
        TeleportRequest request = RegrowthTP.getInstance().getRequestManager().findRequest(from, to);
        if (request != null) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("already-requested")
                .replace("%player%", target.getName()));
            return;
        }

        RegrowthTP.getInstance().getRequestManager().sendRequest(new TeleportRequest(from, to, TeleportDirection.TO));

        PaperColor.handler().sendMessage(target, RegrowthTP.getInstance().getConfigManager().getMessage("received-teleport-request")
            .replace("%player%", player.getName()));
        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("sent-request")
            .replace("%player%", target.getName()));
    }

    @Command({"tp invite", "tpi"})
    @CommandPermission(value = "tp.invite", defaultAccess = PermissionDefault.TRUE)
    public void invite(BukkitCommandActor actor, Player target) {
        Player player = actor.requirePlayer();
        if (player == target) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("self-request"));
            return;
        }

        TPUser targetUser = RegrowthTP.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (targetUser != null && !targetUser.areRequestsEnabled()) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("requests-disabled")
                .replace("%player%", target.getName()));
            return;
        }

        UUID from = player.getUniqueId();
        UUID to = target.getUniqueId();
        TeleportRequest request = RegrowthTP.getInstance().getRequestManager().findRequest(from, to);
        if (request != null) {
            PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("already-requested")
                .replace("%player%", target.getName()));
            return;
        }

        RegrowthTP.getInstance().getRequestManager().sendRequest(new TeleportRequest(from, to, TeleportDirection.SUMMON));

        PaperColor.handler().sendMessage(target, RegrowthTP.getInstance().getConfigManager().getMessage("received-summon-request")
            .replace("%player%", player.getName()));
        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("sent-request")
            .replace("%player%", target.getName()));
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
                PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("no-requests"));
            } else {
                PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("no-request")
                    .replace("%player%", target.getName()));
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
                PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("no-requests"));
            } else {
                PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("no-request")
                    .replace("%player%", target.getName()));
            }
            return;
        }

        if (target == null) {
            target = Bukkit.getPlayer(request.from());
        }

        requestManager.removeRequest(request);
        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("denied-request")
            .replace("%player%", target.getName()));
        target.sendMessage(PaperColor.handler().translate(RegrowthTP.getInstance().getConfigManager().getMessage("request-denied")
            .replace("%player%", player.getName())));
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

        PaperColor.handler().sendMessage(player, RegrowthTP.getInstance().getConfigManager().getMessage("toggle-requests")
            .replace("%status%", user.areRequestsEnabled() ? "enabled" : "disabled"));
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
