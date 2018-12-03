package me.rellynn.foxmc.bungeeapi.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 06/05/2017.
 * FoxMC Network.
 */
public class SetRankCommand extends DefaultCommand {

    public SetRankCommand() {
        super("setrank");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !FoxAPI.getPlayer((ProxiedPlayer) sender).isAtLeast(Rank.OWNER)) {
            return;
        } else if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/setrank <player> <rank>"));
            return;
        }
        UUID uuid = FoxAPI.get().getUuidTranslator().getUUID(args[0], true);
        if (uuid == null) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find §e" + args[0] + "§c!"));
            return;
        }
        FoxAPI.get().runAsync(() -> {
            Rank rank = Rank.getById(args[1]);
            FoxAPI.get().getPlayersHandler().getPlayer(uuid).setRank(rank);
            sender.sendMessage(TextComponent.fromLegacyText("§aRank §b" + rank.getName() + " §aset to §b" + args[0]));
        });
    }
}
