package me.rellynn.foxmc.bungeeapi.moderation.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 18/05/2017.
 * FoxMC Network.
 */
public class ReportCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();
    private Cache<UUID, Long> antiSpam = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.SECONDS).build();

    public ReportCommand() {
        super("report", Rank.DEFAULT);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        } else if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/report <player> <reason>"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (antiSpam.getIfPresent(player.getUniqueId()) != null) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou can only report once every §e15 seconds§c!"));
            return;
        }
        UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0]);
        String serverName = uuid != null ? foxAPI.getNetworkHandler().getServer(uuid) : null;
        if (serverName == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cThe player §e" + args[0] + " §cisn't online!"));
            return;
        } else if (player.getUniqueId().equals(uuid)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou can't report yourself!"));
            return;
        } else if (!canSanction(player, uuid)) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou can't report this player!"));
            return;
        }
        String target = foxAPI.getUuidTranslator().getName(uuid);
        String reason = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/modtp " + target);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClick to TP!"));
        BaseComponent[] components = TextComponent.fromLegacyText(PREFIX + "§e" + player.getName() + " §6reported §e" + target + " §o(" + serverName + ") §6for §r" + reason);
        for (BaseComponent component : components) {
            component.setClickEvent(clickEvent);
            component.setHoverEvent(hoverEvent);
        }
        sendModMessage(components);
        player.sendMessage(TextComponent.fromLegacyText("§aThanks for reporting §e" + target + "§a!"));
        antiSpam.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
