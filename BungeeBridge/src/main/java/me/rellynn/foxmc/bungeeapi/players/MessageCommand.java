package me.rellynn.foxmc.bungeeapi.players;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.players.packets.PlayerMessagePacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.commands.DefaultCommand;
import me.rellynn.foxmc.bungeeapi.moderation.commands.MuteCommand;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 18/04/2017.
 * FoxMC Network.
 */
public class MessageCommand extends DefaultCommand {
    private static final FoxAPI foxAPI = FoxAPI.get();
    private static final Cache<UUID, UUID> lastMessageSender = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    private static void sendMessage(CommandSender sender, UUID uuid, String message) {
        foxAPI.getSqlManager().execute(() -> {
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cThe player isn't online."));
                return;
            }
            PlayerData target = foxAPI.getPlayersHandler().getPlayer(uuid);
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                // Reply bypass
                if (!uuid.equals(lastMessageSender.getIfPresent(player.getUniqueId()))) {
                    MuteCommand.MuteData muteData = MuteCommand.getMuteData(player);
                    // Check for mute
                    if (muteData != null) {
                        sender.sendMessage(TextComponent.fromLegacyText("§cYou have been muted §r" + TimeUtils.getDuration(muteData.getUntil() - System.currentTimeMillis()) + " §cfor §r" + muteData.getReason()));
                        return;
                    }
                    // Moderation bypass
                    if (FoxAPI.getPlayer(player).getModLevel() == 0) {
                        if (Settings.privateMessages.is(target, SettingValue.DISABLED)) {
                            sender.sendMessage(TextComponent.fromLegacyText(target.getDisplayName() + " §chas disabled private messages."));
                            return;
                        } else if (Settings.privateMessages.is(target, SettingValue.FRIENDS) && !target.hasFriend(player.getUniqueId())) {
                            sender.sendMessage(TextComponent.fromLegacyText(target.getDisplayName() + " §callows private messages from friends only."));
                            return;
                        }
                    }
                }
                // Used for reply command
                lastMessageSender.put(uuid, player.getUniqueId());
            }
            TextComponent content = new TextComponent(message);
            content.setColor(ChatColor.LIGHT_PURPLE);
            content.setItalic(true);
            BaseComponent[] senderMsg = new ComponentBuilder("§3To §7" + target.getName() + ": ")
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + target.getName() + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to send a new message").create()))
                    .append(message)
                    .color(ChatColor.LIGHT_PURPLE)
                    .italic(true)
                    .create();
            sender.sendMessage(senderMsg);
            BaseComponent[] targetMessage = new ComponentBuilder("§3From §7" + sender.getName() + ": ")
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + sender.getName() + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to reply").create()))
                    .append(message)
                    .color(ChatColor.LIGHT_PURPLE)
                    .italic(true)
                    .create();
            FProtocolManager.get().sendToProxy(proxyName, new PlayerMessagePacket(uuid, targetMessage));
        });
    }

    static void reply(ProxiedPlayer player, String message) {
        UUID target = lastMessageSender.getIfPresent(player.getUniqueId());
        if (target == null) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou don't have any player to reply!"));
            return;
        }
        sendMessage(player, target, message);
    }

    MessageCommand() {
        super("msg", "m", "w", "tell");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/msg <player> <message>"));
            return;
        }
        UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0]);
        if (uuid == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cThe player isn't online."));
            return;
        }
        sendMessage(sender, uuid, StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " "));
    }
}
