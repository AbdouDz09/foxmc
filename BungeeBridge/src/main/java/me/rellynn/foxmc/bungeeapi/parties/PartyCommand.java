package me.rellynn.foxmc.bungeeapi.parties;

import me.rellynn.foxmc.api.parties.PartiesHandler;
import me.rellynn.foxmc.api.parties.packets.PartyMessagePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerInvitePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerKickPacket;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.players.packets.PlayerMessagePacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.commands.DefaultCommand;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 08/05/2017.
 * FoxMC Network.
 */
public class PartyCommand extends DefaultCommand implements Listener {
    public static final String PREFIX = "§7[§dParty§7] §r";

    private FoxAPI foxAPI = FoxAPI.get();
    private PartiesHandler partiesManager;

    PartyCommand(PartiesManager partiesManager) {
        super("party", "p");
        this.partiesManager = partiesManager;
        ProxyServer.getInstance().getPluginManager().registerListener(BridgePlugin.get(), this);
    }

    private void printHelp(ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText("§9----------- Party Help -----------"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party invite <player> §8- §7send a party invitation to player"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party accept <player> §8- §7accept player party invitation"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party decline <player> §8- §7decline player party invitation"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party kick <player> §8- §7kick player from your party"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party promote <player> §8- §7promote player to the leader rank"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party chat <message> §8- §7send a message in party chat"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party leave §8- §7leaves the current party"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party list §8- §7lists party members"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party disband §8- §7disbands your party"));
        player.sendMessage(TextComponent.fromLegacyText("§6» §e/party help §8- §7prints this help message"));
    }

    private void sendMessage(UUID party, String message) {
        FProtocolManager.get().broadcastProxies(new PartyMessagePacket(party, TextComponent.fromLegacyText(PREFIX + message)));
    }

    private void promoteLeader(UUID party, UUID uuid) {
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            jedis.set("party:" + party + ":leader", uuid + "");
        }
    }

    private void handleLeaderLeave(UUID party) {
        Set<UUID> onlineMembers = partiesManager.getMembers(party).stream().filter(uuid -> foxAPI.getNetworkHandler().isOnline(uuid)).collect(Collectors.toSet());
        if (onlineMembers.isEmpty()) {
            partiesManager.disbandParty(party);
        } else {
            // Promote the next player to leader rank
            UUID newLeader = onlineMembers.iterator().next();
            promoteLeader(party, newLeader);
            sendMessage(party, "§c**" + foxAPI.getUuidTranslator().getName(newLeader) + " §ais now the party leader.");
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent evt) {
        foxAPI.runAsync(() -> {
            UUID uuid = evt.getPlayer().getUniqueId();
            UUID party = partiesManager.getPlayerParty(uuid);
            if (party != null && uuid.equals(partiesManager.getLeader(party))) {
                // Give the leadership to another player if the disconnected player was a party leader.
                handleLeaderLeave(party);
            }
        });
    }

    private void listMembers(ProxiedPlayer player) {
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        player.sendMessage(TextComponent.fromLegacyText("§9--------- Party Members ---------"));
        if (party == null) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou aren't in a party! Create your party by using the command §e/party invite §l<player>"));
            return;
        }
        UUID leader = partiesManager.getLeader(party);
        partiesManager.getMembers(party).forEach(uuid -> {
            String name = foxAPI.getUuidTranslator().getName(uuid);
            if (name == null) {
                name = "??? UNKNOWN ???";
            }
            player.sendMessage(TextComponent.fromLegacyText("§6» " + (!uuid.equals(leader) ? "§e" + name : ("§c**" + name + " [LEADER]")) + " §8- " + (foxAPI.getNetworkHandler().isOnline(uuid) ? "§aonline" : "§coffline")));
        });
    }

    private void printError(String message, ProxiedPlayer player) {
        player.sendMessage(TextComponent.fromLegacyText(PREFIX + "§c" + message));
    }

    private void invitePlayer(ProxiedPlayer player, String targetName) {
        UUID uuid = foxAPI.getUuidTranslator().getUUID(targetName);
        PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
        if (target == null) {
            printError("Unable to find the player §e" + targetName, player);
            return;
        } else if (uuid.equals(player.getUniqueId())) {
            printError("You can't invite yourself!", player);
            return;
        }
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        if (party != null && !partiesManager.isLeader(party, player.getUniqueId())) {
            printError("You must be the party leader!", player);
            return;
        }
        String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
        if (proxyName == null) {
            printError(target.getDisplayName() + " §cisn't online!", player);
            return;
        } else if (Settings.partyInvites.is(target, SettingValue.DISABLED)) {
            printError(target.getDisplayName() + " §chas disabled party invitations!", player);
            return;
        } else if (Settings.partyInvites.is(target, SettingValue.FRIENDS) && !target.hasFriend(player.getUniqueId())) {
            printError(target.getDisplayName() + " §callows invitations from friends only!", player);
            return;
        } else if (partiesManager.getPlayerParty(uuid) != null) {
            printError(target.getDisplayName() + " §cis already in a party!", player);
            return;
        }
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            String key = "partyinvites:" + player.getUniqueId() + ":" + uuid;
            if (jedis.exists(key)) {
                printError("You already sent an invitation to this player!", player);
            } else {
                jedis.setex(key, 60, System.currentTimeMillis() + "");
                player.sendMessage(TextComponent.fromLegacyText("§aYou invited " + target.getDisplayName() + " §ato your party!"));
                FProtocolManager.get().sendToProxy(proxyName, new PartyPlayerInvitePacket(player.getName(), player.getDisplayName(), uuid));
            }
        }
    }

    private void declineInvite(ProxiedPlayer player, String fromName) {
        UUID sender = foxAPI.getUuidTranslator().getUUID(fromName);
        PlayerData from = sender != null ? foxAPI.getPlayersHandler().getPlayer(sender, false) : null;
        if (from == null) {
            printError("Unable to find the player §e" + fromName, player);
            return;
        }
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            if (jedis.del("partyinvites:" + sender + ":" + player.getUniqueId()) == 0) {
                printError("You don't have any party invitation from " + from.getDisplayName(), player);
            } else {
                player.sendMessage(TextComponent.fromLegacyText("§cYou declined the party invitation from " + from.getDisplayName()));
            }
        }
    }

    private void acceptInvite(ProxiedPlayer player, String fromName) {
        UUID sender = foxAPI.getUuidTranslator().getUUID(fromName);
        PlayerData from = sender != null ? foxAPI.getPlayersHandler().getPlayer(sender, false) : null;
        if (from == null) {
            printError("Unable to find the player §e" + fromName, player);
            return;
        }
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            if (jedis.del("partyinvites:" + sender + ":" + player.getUniqueId()) == 0) {
                printError("You don't have any party invitation from " + from.getDisplayName(), player);
                return;
            }
            UUID party = partiesManager.getPlayerParty(sender);
            if (party == null) {
                // Create new party
                party = UUID.randomUUID();
                jedis.set("party:" + party + ":leader", sender + "");
                jedis.sadd("party:" + party + ":members", sender + "");
            }
            jedis.sadd("party:" + party + ":members", player.getUniqueId() + "");
            sendMessage(party, player.getDisplayName() + " §ajoined the party!");
        }
    }

    private void removeMember(UUID party, UUID member, boolean checkLeader) {
        try (Jedis jedis = foxAPI.getRedisManager().getResource()) {
            jedis.srem("party:" + party + ":members", member + "");
            if (checkLeader && partiesManager.isLeader(party, member)) {
                handleLeaderLeave(party);
            }
        }
    }

    private void kickMember(ProxiedPlayer player, String member) {
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        if (party == null) {
            printError("You must be in a party!", player);
            return;
        } else if (!partiesManager.isLeader(party, player.getUniqueId())) {
            printError("You must be the party leader to kick members!", player);
            return;
        }
        UUID uuid = foxAPI.getUuidTranslator().getUUID(member);
        if (uuid == null || !partiesManager.hasPlayer(party, uuid)) {
            printError("§e" + member + " §cisn't a member of your party!", player);
        } else {
            removeMember(party, uuid, false);
            FProtocolManager.get().broadcastProxies(new PartyPlayerKickPacket(party, player.getName(), uuid, foxAPI.getPlayersHandler().getPlayer(uuid).getDisplayName()));
        }
    }

    private void partyChat(ProxiedPlayer player, String message) {
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        if (party == null) {
            printError("You aren't in a party! Create a party by using the command §e/party invite <player> §cto chat with your friends!", player);
            return;
        }
        sendMessage(party, (partiesManager.isLeader(party, player.getUniqueId()) ? "§c**" : "§e") + player.getName() + ": §r" + message);
    }

    private void leaveParty(ProxiedPlayer player) {
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        if (party == null) {
            printError("You must be in a party!", player);
            return;
        }
        removeMember(party, player.getUniqueId(), true);
        player.sendMessage(TextComponent.fromLegacyText(PREFIX + "§aYou left the party!"));
        sendMessage(party, "§e" + player.getName() + " §cleft the party!");
    }

    private void disbandParty(ProxiedPlayer player) {
        UUID party = partiesManager.getPlayerParty(player.getUniqueId());
        if (party == null) {
            printError("You must be in a party!", player);
            return;
        } else if (!partiesManager.isLeader(party, player.getUniqueId())) {
            printError("You must be the party leader!", player);
            return;
        }
        partiesManager.disbandParty(party);
        player.sendMessage(TextComponent.fromLegacyText(PREFIX + "§cYou disbanded your party."));
        BaseComponent[] message = TextComponent.fromLegacyText(PREFIX + "§c**" + player.getName() + " disbanded the party!");
        // Send message to members
        partiesManager.getMembers(party).forEach(uuid -> {
            if (player.getUniqueId().equals(uuid)) {
                return;
            }
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName != null) {
                FProtocolManager.get().sendToProxy(proxyName, new PlayerMessagePacket(uuid, message));
            }
        });
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
        foxAPI.runAsync(() -> {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "help":
                    printHelp(player);
                    break;
                case "list":
                    listMembers(player);
                    break;
                case "add":
                case "invite":
                    if (args.length < 2) {
                        printError("§cUsage: §e/party invite §l<player>", player);
                        break;
                    }
                    invitePlayer(player, args[1]);
                    break;
                case "decline":
                    if (args.length < 2) {
                        printError("§cUsage: §e/party decline §l<player>", player);
                        break;
                    }
                    declineInvite(player, args[1]);
                    break;
                case "accept":
                    if (args.length < 2) {
                        printError("§cUsage: §e/party accept §l<player>", player);
                        break;
                    }
                    acceptInvite(player, args[1]);
                    break;
                case "kick":
                case "remove":
                    if (args.length < 2) {
                        printError("§cUsage: §e/party kick <player>", player);
                        break;
                    }
                    kickMember(player, args[1]);
                    break;
                case "lead":
                case "leader":
                case "promote":
                    if (args.length < 2) {
                        player.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/party promote §l<player>"));
                        break;
                    }
                    UUID party = partiesManager.getPlayerParty(player.getUniqueId());
                    if (party == null) {
                        printError("You must be in a party!", player);
                        break;
                    } else if (!partiesManager.isLeader(party, player.getUniqueId())) {
                        printError("You must be party leader!", player);
                        break;
                    }
                    UUID uuid = foxAPI.getUuidTranslator().getUUID(args[1]);
                    if (player.getUniqueId().equals(uuid)) {
                        printError("You're already the party leader!", player);
                        break;
                    }
                    PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
                    if (target == null) {
                        printError("Unable to find the player!", player);
                        break;
                    } else if (!partiesManager.hasPlayer(party, uuid)) {
                        printError(target.getDisplayName() + " §cisn't a member of your party!", player);
                        break;
                    } else if (!foxAPI.getNetworkHandler().isOnline(uuid)) {
                        printError(target.getDisplayName() + " §cis offline!", player);
                        break;
                    }
                    promoteLeader(party, uuid);
                    sendMessage(party, "§c**" + player.getName() + " §apromoted §e" + target.getName() + " §ato leader.");
                    break;
                case "c":
                case "chat":
                    if (args.length < 2) {
                        player.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/party chat §l<message>"));
                        break;
                    }
                    String[] message = Arrays.copyOfRange(args, 1, args.length);
                    partyChat(player, StringUtils.join(message, " "));
                    break;
                case "leave":
                    leaveParty(player);
                    break;
                case "disband":
                    disbandParty(player);
                    break;
                default:
                    invitePlayer(player, args[0]);
            }
        });
    }
}
