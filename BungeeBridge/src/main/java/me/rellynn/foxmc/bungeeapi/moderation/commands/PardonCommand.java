package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.BanEntry;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
public class PardonCommand extends ModerationCommand {
    private FoxAPI foxAPI = FoxAPI.get();

    public PardonCommand() {
        super("pardon", Rank.MODERATOR, "unban");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/pardon <player>"));
            return;
        }
        foxAPI.getSqlManager().execute(() -> {
            UUID uuid = foxAPI.getUuidTranslator().getUUID(args[0], true);
            PlayerData target = uuid != null ? foxAPI.getPlayersHandler().getPlayer(uuid, false) : null;
            if (target == null) {
                sender.sendMessage(TextComponent.fromLegacyText("§cUnable to find the player §e" + args[0] + "§c!"));
                return;
            }
            BanEntry banEntry = target.getLastBan();
            if (banEntry == null || !banEntry.isActive()) {
                sender.sendMessage(TextComponent.fromLegacyText("§e" + target.getName() + " §cisn't banned!"));
                return;
            }
            banEntry.unban(!(sender instanceof ProxiedPlayer) ? "CONSOLE" : ((ProxiedPlayer) sender).getUniqueId() + "");
            sendModMessage(TextComponent.fromLegacyText(PREFIX + "§e" + target.getName() + " §6has been unbanned by §e" + sender.getName() + "§6!"));
        });
    }
}
