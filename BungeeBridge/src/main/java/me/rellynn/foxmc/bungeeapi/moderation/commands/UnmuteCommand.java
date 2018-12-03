package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.moderation.packets.PlayerUnmutePacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.MuteEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 16/05/2017.
 * FoxMC Network.
 */
public class UnmuteCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public UnmuteCommand() {
        super("unmute", Rank.HELPER);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/unmute <player>"));
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
            if (target == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find the player §e" + args[0] + "§c!"));
                return;
            }
            MuteEntry muteEntry = target.getLastMute();
            if (muteEntry == null || !muteEntry.isActive()) {
                sender.sendMessage(TextComponent.fromLegacyText("§e" + target.getName() + " §cisn't muted!"));
                return;
            }
            MuteCommand.removeMuteData(uuid);
            muteEntry.unmute(!(sender instanceof ProxiedPlayer) ? "CONSOLE" : ((ProxiedPlayer) sender).getUniqueId() + "");
            sendModMessage(TextComponent.fromLegacyText(PREFIX + "§e" + target.getName() + " §6has been unmuted by §e" + sender.getName() + "§6!"));
            String proxyName = foxAPI.getNetworkHandler().getProxy(uuid);
            if (proxyName != null) {
                FProtocolManager.get().sendToProxy(proxyName, new PlayerUnmutePacket(uuid));
            }
        });
    }
}
