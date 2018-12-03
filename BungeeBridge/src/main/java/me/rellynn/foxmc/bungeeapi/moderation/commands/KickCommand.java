package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.moderation.packets.PlayerKickPacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.KickEntry;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
public class KickCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public KickCommand() {
        super("kick", Rank.HELPER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/kick <player> [reason]"));
            return;
        }
        FoxAPI.get().getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            String proxyName = uuid != null ? foxAPI.getNetworkHandler().getProxy(uuid) : null;
            if (proxyName == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cThe player §e" + args[0] + "§c isn't online!"));
                return;
            } else if (sender instanceof ProxiedPlayer && !canSanction((ProxiedPlayer) sender, uuid)) {
                sender.sendMessage(TextComponent.fromLegacyText("§cYou may not kick this player!"));
                return;
            }
            String reason = args.length < 2 ? "No reason specified" : StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
            sendModMessage(TextComponent.fromLegacyText(PREFIX + "§e" + foxAPI.getUuidTranslator().getName(uuid) + " §6has been kicked by §e" + sender.getName() + " §6for §r" + reason));
            new KickEntry(uuid, reason, !(sender instanceof ProxiedPlayer) ? "CONSOLE" : ((ProxiedPlayer) sender).getUniqueId() + "");
            FProtocolManager.get().sendToProxy(proxyName, new PlayerKickPacket(uuid, "§cYou've been kicked! Reason: §r" + reason));
        });
    }
}
