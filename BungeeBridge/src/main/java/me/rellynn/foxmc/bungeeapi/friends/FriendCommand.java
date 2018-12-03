package me.rellynn.foxmc.bungeeapi.friends;

import me.rellynn.foxmc.api.friends.packets.FriendRequestPacket;
import me.rellynn.foxmc.api.friends.packets.FriendResponsePacket;
import me.rellynn.foxmc.api.friends.packets.FriendTeleportPacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.commands.DefaultCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class FriendCommand extends DefaultCommand {
    public static final String PREFIX = "§7[§dFriend§7] §r";

    private FoxAPI foxAPI = FoxAPI.get();

    FriendCommand() {
        super("friend", "friends", "f");
    }

    private void printError(String message, ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText(PREFIX + "§c" + message));
    }

    private void printHelp(ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText("§9---------- Friends Help ----------"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend invite <player> §8- §7send a friend request to player"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend accept <player> §8- §7accept player friend request"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend decline <player> §8- §7decline player friend request"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend remove <player> §8- §7remove player from your friends"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend tp <player> §8- §7teleport you to friend server"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend requests §8- §7lists received friend requests"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend list §8- §7lists your friends"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/friend help §8- §7prints this help message"));
    }

    private void listFriends(ProxiedPlayer player) {
        Set<UUID> friends = FoxAPI.getPlayer(player).getFriends();
        player.sendMessage(TextComponent.fromLegacyText("§9---------- My Friends -----------"));
        if (friends.isEmpty()) {
            player.sendMessage(TextComponent.fromLegacyText("§cSorry but you don't have any friend!"));
            return;
        }
        for (UUID uuid : friends) {
            PlayerData target = FoxAPI.get().getPlayersHandler().getPlayer(uuid);
            String serverName = foxAPI.getNetworkHandler().getServer(uuid);
            BaseComponent[] components = new ComponentBuilder("§6» §r" + target.getDisplayName() + " §8- " + (serverName == null ? "§coffline" : ("§aonline (" + serverName + ")")))
                    .append(" §c[Remove]")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cClick to remove friend")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove " + target.getName()))
                    .create();
            player.sendMessage(components);
        }
    }

    private void listRequests(ProxiedPlayer player) {
        Set<UUID> requests = FoxAPI.getPlayer(player).getFriendRequests();
        player.sendMessage(TextComponent.fromLegacyText("§9----------- Requests -----------"));
        if (requests.isEmpty()) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou don't have any request!"));
            return;
        }
        for (UUID uuid : requests) {
            String name = foxAPI.getUuidTranslator().getName(uuid, true);
            if (name == null) {
                name = "??? UNKNOWN ???";
            }
            BaseComponent[] components = new ComponentBuilder("§8» §7Request from: §e" + name + " §8- ")
                    .append("§a[ACCEPT]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClick to accept friend request"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + name))
                    .append(" §7or ", ComponentBuilder.FormatRetention.NONE)
                    .append("§c[DECLINE]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cClick to decline friend request"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + name))
                    .create();
            player.sendMessage(components);
        }
    }

    private void sendRequest(ProxiedPlayer player, String targetName) {
        UUID uuid = foxAPI.getUuidTranslator().getUUID(targetName);
        if (player.getUniqueId().equals(uuid)) {
            printError("You can't invite yourself!", player);
            return;
        }
        PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (target == null) {
            printError("Unable to find the player §e" + targetName, player);
            return;
        }
        String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
        if (proxyName == null) {
            printError(target.getDisplayName() + " §cisn't online!", player);
            return;
        } else if (!Settings.friendRequests.is(target, SettingValue.ENABLED)) {
            printError(target.getDisplayName() + " §chas disabled friend requests!", player);
            return;
        } else if (FoxAPI.getPlayer(player).hasFriend(uuid)) {
            printError("You're already friends with §r" + target.getDisplayName(), player);
            return;
        }
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            String key = "friendrequests:" + player.getUniqueId() + ":" + uuid;
            if (jedis.exists(key)) {
                printError("You already sent a friend request to §r" + target.getDisplayName(), player);
                return;
            }
            jedis.setex(key, 300, System.currentTimeMillis() + "");
            player.sendMessage(TextComponent.fromLegacyText("§aYou sent a friend request to §r" + target.getDisplayName()));
            FProtocolManager.get().sendToProxy(proxyName, new FriendRequestPacket(player.getUniqueId(), uuid));
        }
    }

    private void denyRequest(ProxiedPlayer player, String fromName) {
        UUID uuid = foxAPI.getUuidTranslator().getUUID(fromName);
        PlayerData from = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (from == null) {
            printError("Unable to find the player §e" + fromName, player);
            return;
        } else if (!FoxAPI.getPlayer(player).declineFriendRequest(uuid)) {
            printError("You don't have any friend request from §r" + from.getDisplayName(), player);
            return;
        }
        player.sendMessage(TextComponent.fromLegacyText("§cYou declined the friend request from §r" + from.getDisplayName()));
        String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
        if (proxyName != null) {
            FProtocolManager.get().sendToProxy(proxyName, new FriendResponsePacket(player.getUniqueId(), uuid, false));
        }
    }

    private void grantRequest(ProxiedPlayer player, String fromName) {
        UUID uuid = foxAPI.getUuidTranslator().getUUID(fromName);
        PlayerData from = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (from == null) {
            printError("Unable to find the player §e" + fromName, player);
            return;
        }
        PlayerData data = FoxAPI.getPlayer(player);
        if (data.hasFriend(uuid)) {
            printError("You're already friends with §r" + from.getDisplayName(), player);
        } else if (!data.acceptFriendRequest(uuid)) {
            printError("You don't have any friend request from §r" + from.getDisplayName(), player);
        } else {
            player.sendMessage(TextComponent.fromLegacyText("§aYou accepted the friend request from §r" + from.getDisplayName()));
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName != null) {
                FProtocolManager.get().sendToProxy(proxyName, new FriendResponsePacket(player.getUniqueId(), uuid, true));
            }
        }
    }

    private void removeFriend(ProxiedPlayer player, String friendName) {
        UUID uuid = foxAPI.getUuidTranslator().getUUID(friendName);
        PlayerData friend = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (friend == null) {
            printError("Unable to find the player §e" + friendName, player);
            return;
        } else if (!FoxAPI.getPlayer(player).removeFriend(uuid)) {
            printError("You aren't friends with §r" + friend.getDisplayName(), player);
            return;
        }
        player.sendMessage(TextComponent.fromLegacyText("§cYou are no longer friends with §r" + friend.getDisplayName()));
    }

    private void teleportToFriend(ProxiedPlayer player, String friendName) {
        PlayerData data = FoxAPI.getPlayer(player);
        if (!data.isAtLeast(Rank.FOX)) {
            printError("You must be " + Rank.FOX.getPrefix() + " §cor higher to teleport to your friend!", player);
            return;
        }
        UUID uuid = foxAPI.getUuidTranslator().getUUID(friendName);
        if (player.getUniqueId().equals(uuid)) {
            printError("You can't teleport to yourself!", player);
            return;
        }
        PlayerData friend = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (friend == null) {
            printError("Unable to find the player §e" + friendName, player);
            return;
        } else if (!data.hasFriend(uuid)) {
            printError("You're not friends with §r" + friend.getDisplayName(), player);
            return;
        }
        String serverName = foxAPI.getNetworkHandler().getServer(uuid);
        if (serverName == null) {
            printError(friend.getDisplayName() + " §cis offline!", player);
        } else {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);
            if (info == null) {
                printError("Unable to find friend server!", player);
                return;
            } else if (info != player.getServer().getInfo()) {
                player.connect(info);
            }
            player.sendMessage(TextComponent.fromLegacyText("§7Teleporting to §r" + data.getDisplayName() + "§7..."));
            FProtocolManager.get().sendToServer(serverName, new FriendTeleportPacket(player.getUniqueId(), uuid));
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length == 0) {
            printHelp(player);
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "help":
                    printHelp(player);
                    return;
                case "list":
                    listFriends(player);
                    return;
                case "requests":
                    listRequests(player);
                    return;
                case "add":
                case "invite":
                    if (args.length < 2) {
                        printError("Use §e/friend invite §l<player>", player);
                        break;
                    }
                    sendRequest(player, args[1]);
                    break;
                case "deny":
                case "decline":
                    if (args.length < 2) {
                        printError("Use §e/friend decline §l<player>", player);
                        break;
                    }
                    denyRequest(player, args[1]);
                    break;
                case "accept":
                    if (args.length < 2) {
                        printError("Use §e/friend accept §l<player>", player);
                        break;
                    }
                    grantRequest(player, args[1]);
                    break;
                case "remove":
                    if (args.length < 2) {
                        printError("Use §e/friend remove §l<player>", player);
                        break;
                    }
                    removeFriend(player, args[1]);
                    break;
                case "tp":
                case "teleport":
                    if (args.length < 2) {
                        printError("Use §e/friend tp §l<player>", player);
                        break;
                    }
                    teleportToFriend(player, args[1]);
                    break;
                default:
                    sendRequest(player, args[0]);
            }
        });
    }
}
