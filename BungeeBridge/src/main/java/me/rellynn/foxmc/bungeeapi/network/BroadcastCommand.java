package me.rellynn.foxmc.bungeeapi.network;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by gwennaelguich on 20/04/2017.
 * FoxMC Network.
 */
public class BroadcastCommand extends Command {

    BroadcastCommand() {
        super("broadcast", null, "bc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
        } else if (FoxAPI.getPlayer((ProxiedPlayer) sender).isAtLeast(Rank.ADMIN)) {
            if (args.length == 0) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/broadcast <message>"));
                return;
            }
            String message = "[§d*§r] §c" + sender.getName() + " §7» §r" + StringUtils.join(args, " ");
            FoxAPI.get().getNetworkHandler().broadcast(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
