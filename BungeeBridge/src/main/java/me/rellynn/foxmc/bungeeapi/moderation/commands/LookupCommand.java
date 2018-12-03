package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.BanEntry;
import me.rellynn.foxmc.api.players.database.KickEntry;
import me.rellynn.foxmc.api.players.database.MuteEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import me.rellynn.foxmc.bungeeapi.utils.uuid.NameFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Created by gwennaelguich on 16/05/2017.
 * FoxMC Network.
 */
public class LookupCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public LookupCommand() {
        super("lookup", Rank.HELPER);
    }

    private String getName(String str) {
        if (str.split("-").length != 5) {
            return str;
        }
        return foxAPI.getUuidTranslator().getName(UUID.fromString(str), true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/lookup <player>"));
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            PlayerData data = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
            if (data == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find the player §e" + args[0] + "§c!"));
            } else if (args.length == 1) {
                List<String> names = NameFetcher.nameHistoryFromUuid(uuid);
                sender.sendMessage(TextComponent.fromLegacyText("§9--------- Lookup: " + data.getName() + " ---------"));
                sender.sendMessage(TextComponent.fromLegacyText("§eUnique ID §8» §7" + uuid));
                sender.sendMessage(TextComponent.fromLegacyText("§eNames history §8» " + (names == null ? "§cError" : "§a" + StringUtils.join(names.toArray(new String[0]), ", "))));
                sender.sendMessage(TextComponent.fromLegacyText("§eRank §8» " + data.getRank().getName()));
                sender.sendMessage(TextComponent.fromLegacyText("§eJoined the server on §8» §b" + data.getFirstLogin().toString()));
                sender.sendMessage(TextComponent.fromLegacyText("§eLast login on §8» §b" + data.getLastLogin().toString()));
                String lastKnownIP = sender instanceof ProxiedPlayer && !canSanction((ProxiedPlayer) sender, uuid) ? "§cHidden" : "§a" + data.getLastIP();
                sender.sendMessage(TextComponent.fromLegacyText("§eLast known IP §8» " + lastKnownIP));
                String serverName = foxAPI.getNetworkHandler().getServer(uuid);
                sender.sendMessage(new ComponentBuilder("§eServer §8» ")
                        .append(serverName != null ? "§a" + serverName : "§cOffline")
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/modtp " + data.getName()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClick to TP!")))
                        .create());
                HoverEvent detailsHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Show details"));
                sender.sendMessage(new ComponentBuilder("§eHistory §8» §c" + data.getBans().size() + " bans").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lookup " + args[0] + " bans")).event(detailsHover).create());
                sender.sendMessage(new ComponentBuilder("             §8» §c" + data.getKicks().size() + " kicks").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lookup " + args[0] + " kicks")).event(detailsHover).create());
                sender.sendMessage(new ComponentBuilder("             §8» §c" + data.getMutes().size() + " mutes").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lookup " + args[0] + " mutes")).event(detailsHover).create());
                sender.sendMessage(TextComponent.fromLegacyText("      §7Click on an information to show details."));
            } else {
                switch (args[1]) {
                    case "bans":
                        List<BanEntry> bans = data.getBans();
                        sender.sendMessage(TextComponent.fromLegacyText("§9---------- " + data.getName() + " Bans ----------"));
                        if (bans.isEmpty()) {
                            sender.sendMessage(TextComponent.fromLegacyText("§e" + data.getName() + " §chas never been banned!"));
                        } else {
                            for (BanEntry ban : bans) {
                                sender.sendMessage(TextComponent.fromLegacyText("§e--- §r[" + ban.getDate().toString() + "] §e---"));
                                sender.sendMessage(TextComponent.fromLegacyText("§7Banned by: §e" + getName(ban.getBannedBy()) + " §7| Until: §a" + ban.getUntilDate().toString() + " §7| Reason: §r" + ban.getReason()));
                            }
                        }
                        break;
                    case "kicks":
                        List<KickEntry> kicks = data.getKicks();
                        sender.sendMessage(TextComponent.fromLegacyText("§9---------- " + data.getName() + " Kicks ----------"));
                        if (kicks.isEmpty()) {
                            sender.sendMessage(TextComponent.fromLegacyText("§e" + data.getName() + " §chas never been kicked!"));
                        } else {
                            for (KickEntry kick : kicks) {
                                sender.sendMessage(TextComponent.fromLegacyText("§e--- §r[" + kick.getDate().toString() + "] §e---"));
                                sender.sendMessage(TextComponent.fromLegacyText("§7Kicked by: §e" + getName(kick.getKickedBy()) + " §7| Reason: §r" + kick.getReason()));
                            }
                        }
                        break;
                    case "mutes":
                        List<MuteEntry> mutes = data.getMutes();
                        sender.sendMessage(TextComponent.fromLegacyText("§9--------- " + data.getName() + " Mutes ----------"));
                        if (mutes.isEmpty()) {
                            sender.sendMessage(TextComponent.fromLegacyText("§e" + data.getName() + " §chas never been muted!"));
                        } else {
                            for (MuteEntry mute : mutes) {
                                sender.sendMessage(TextComponent.fromLegacyText("§e--- §r[" + mute.getDate().toString() + "] §e---"));
                                sender.sendMessage(TextComponent.fromLegacyText("§7Banned by: §e" + getName(mute.getMutedBy()) + " §7| Until: §a" + mute.getUntilDate().toString() + " §7| Reason: §r" + mute.getReason()));
                            }
                        }
                        break;
                }
            }
        });
    }
}
