package me.rellynn.foxmc.bungeeapi.friends;

import me.rellynn.foxmc.api.friends.FriendsHandler;
import me.rellynn.foxmc.api.friends.packets.FriendConnectPacket;
import me.rellynn.foxmc.api.friends.packets.FriendRequestPacket;
import me.rellynn.foxmc.api.friends.packets.FriendResponsePacket;
import me.rellynn.foxmc.api.friends.packets.FriendTeleportPacket;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static me.rellynn.foxmc.bungeeapi.friends.FriendCommand.PREFIX;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class FriendsManager extends FriendsHandler {

    public FriendsManager() {
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new FriendCommand());
    }

    @Override
    public void onFriendConnect(FriendConnectPacket packet) {
        PlayerData player = FoxAPI.get().getPlayersHandler().getPlayer(packet.getPlayer());
        BaseComponent[] components = TextComponent.fromLegacyText(PREFIX + "§e" + player.getName() + " §ais now online!");
        player.getFriends().forEach(uuid -> {
            ProxiedPlayer friend = ProxyServer.getInstance().getPlayer(uuid);
            if (friend != null) {
                friend.sendMessage(components);
            }
        });
    }

    @Override
    public void onFriendRequest(FriendRequestPacket packet) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(packet.getTarget());
        if (target != null) {
            PlayerData player = FoxAPI.get().getPlayersHandler().getPlayer(packet.getPlayer());
            target.sendMessage(TextComponent.fromLegacyText(PREFIX + player.getDisplayName() + " §7sent you a friend request!"));
            BaseComponent[] components = new ComponentBuilder("")
                    .append("[ACCEPT]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to accept friend request").create()))
                    .append(" or ").reset().color(ChatColor.GRAY)
                    .append("[DECLINE]").reset().color(ChatColor.RED).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + player.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick to decline friend request").create())).create();
            target.sendMessage(components);
        }
    }

    @Override
    public void onFriendResponse(FriendResponsePacket packet) {
        if (packet.isAccepted()) {
            ProxiedPlayer friend = ProxyServer.getInstance().getPlayer(packet.getTarget());
            if (friend != null) {
                PlayerData player = FoxAPI.get().getPlayersHandler().getPlayer(packet.getPlayer());
                friend.sendMessage(TextComponent.fromLegacyText(PREFIX + player.getDisplayName() + " §aaccepted your friend request!"));
            }
        }
    }

    @Override
    public void onFriendTeleport(FriendTeleportPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            String targetServer = FoxAPI.get().getNetworkHandler().getServer(packet.getFriend());
            ServerInfo info = targetServer != null ? ProxyServer.getInstance().getServerInfo(targetServer) : null;
            if (info == null) {
                player.sendMessage(TextComponent.fromLegacyText("§cUnable to find your friend server!"));
                return;
            }
            player.connect(info);
        }
    }
}
