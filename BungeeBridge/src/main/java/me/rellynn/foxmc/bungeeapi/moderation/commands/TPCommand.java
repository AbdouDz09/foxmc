package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.moderation.packets.PlayerTeleportPacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 16/05/2017.
 * FoxMC Network.
 */
public class TPCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public TPCommand() {
        super("modtp", Rank.MODERATOR);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        } else if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/modtp <player>"));
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            String serverName = uuid != null ? foxAPI.getNetworkHandler().getServer(uuid) : null;
            if (serverName == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cThe player §e" + args[0] + " §cisn't online!"));
            } else {
                ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);
                if (info == null) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find player server!"));
                    return;
                } else if (info != player.getServer().getInfo()) {
                    player.connect(info);
                }
                FProtocolManager.get().sendToServer(serverName, new PlayerTeleportPacket(player.getUniqueId(), uuid));
                sendModMessage(TextComponent.fromLegacyText(PREFIX + "§e" + player.getName() + " §6teleported to §e" + foxAPI.getUuidTranslator().getName(uuid) + "§6!"));
            }
        });
    }
}
