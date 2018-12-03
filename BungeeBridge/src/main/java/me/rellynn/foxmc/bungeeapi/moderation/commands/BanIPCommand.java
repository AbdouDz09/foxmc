package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.moderation.packets.PlayerKickPacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.BanIPEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.utils.TimeUtils;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by gwennaelguich on 26/06/2017.
 * FoxMC Network.
 */
public class BanIPCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public BanIPCommand() {
        super("banip", Rank.ADMIN);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/banip <player> <duration> <reason>"));
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
            if (target == null || target.getLastIP() == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find the player §e" + args[0] + "§c!"));
                return;
            } else if (sender instanceof ProxiedPlayer && !canSanction((ProxiedPlayer) sender, uuid)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cYou may not ban this player!"));
                return;
            }
            long duration;
            try {
                duration = TimeUtils.getDuration(args[1]);
            } catch (Exception ex) {
                sender.sendMessage(TextComponent.fromLegacyText("§cThe ban duration is invalid!"));
                return;
            }
            String durationStr = TimeUtils.getDuration(duration);
            String reason = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), " ");
            sendModMessage(TextComponent.fromLegacyText(PREFIX + "§6IP §e" + target.getLastIP() + " (" + target.getName() + ") §6has been banned §e" + durationStr + " §6by §e" + sender.getName() + " §6for §r" + reason));
            new BanIPEntry(target.getLastIP(), reason, !(sender instanceof ProxiedPlayer) ? "CONSOLE" : ((ProxiedPlayer) sender).getUniqueId() + "", System.currentTimeMillis() + duration);
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName != null) {
                FProtocolManager.get().sendToProxy(proxyName, new PlayerKickPacket(uuid, "§cYou've been banned for §e" + durationStr + "§c! Reason: §r" + reason));
            }
        });
    }
}
