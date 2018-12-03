package me.rellynn.foxmc.bungeeapi.moderation;

import me.rellynn.foxmc.api.moderation.ModerationHandler;
import me.rellynn.foxmc.api.moderation.packets.*;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.moderation.commands.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class ModerationManager extends ModerationHandler {

    public ModerationManager() {
        // Register commands
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerCommand(BridgePlugin.get(), new BanCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new PardonCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new KickCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new MuteCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new UnmuteCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new LookupCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new ModCommand());
        pluginManager.registerCommand(BridgePlugin.get(), new TPCommand());
    }

    @Override
    protected void handleMessage(ModerationMessagePacket packet) {
        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
            if (FoxAPI.getPlayer(online).isAtLeast(Rank.HELPER)) {
                online.sendMessage(packet.getMessage());
            }
        }
    }

    @Override
    protected void handleTeleport(PlayerTeleportPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(packet.getTarget());
        if (player != null && target != null && player.getServer() != target.getServer()) {
            player.connect(target.getServer().getInfo());
        }
    }

    @Override
    protected void handleKick(PlayerKickPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            player.disconnect(TextComponent.fromLegacyText(packet.getReason()));
        }
    }

    @Override
    protected void handleMute(PlayerMutePacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            MuteCommand.addMuteData(player, packet.getReason(), packet.getDuration());
            player.sendMessage(TextComponent.fromLegacyText("§cYou've been muted §e" + TimeUtils.getDuration(packet.getDuration()) + " §cfor §r" + packet.getReason()));
        }
    }

    @Override
    protected void handleUnmute(PlayerUnmutePacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getTarget());
        if (player != null) {
            MuteCommand.removeMuteData(packet.getTarget());
            player.sendMessage(TextComponent.fromLegacyText("§eYou have been unmuted!"));
        }
    }
}

