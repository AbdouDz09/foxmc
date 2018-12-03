package me.rellynn.foxmc.bungeeapi.players;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by gwennaelguich on 18/05/2017.
 * FoxMC Network.
 */
public class NickCommand extends Command {
    private static final Pattern PATTERN = Pattern.compile("[a-z0-9_]{3,16}", Pattern.CASE_INSENSITIVE);

    NickCommand() {
        super("nick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        PlayerData data = FoxAPI.getPlayer(player);
        if (!data.isAtLeast(Rank.YOUTUBER)) {
            player.sendMessage(TextComponent.fromLegacyText("§cYou're not allowed to do that!"));
            return;
        } else if (!data.hasNick() && args.length == 0) {
            player.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/nick [name]"));
            return;
        } else if (args.length == 0) {
            data.setNickName(player.getName());
        } else if (args.length > 0) {
            if (!PATTERN.matcher(args[0]).matches()) {
                player.sendMessage(TextComponent.fromLegacyText("§cThe nickname should be alphanumeric."));
                return;
            }
            UUID uuid = FoxAPI.get().getUuidTranslator().getUUID(args[0], true);
            if (uuid != null && !player.getUniqueId().equals(uuid) && FoxAPI.get().getPlayersHandler().getPlayer(uuid, false) != null) {
                player.sendMessage(TextComponent.fromLegacyText("§e" + args[0] + " §cis already in use! Please choose another nick."));
                return;
            }
            data.setNickName(args[0]);
        }
        player.setDisplayName(data.getDisplayName());
        player.sendMessage(TextComponent.fromLegacyText("§aYou are known as §r" + player.getDisplayName() + "§a."));
    }
}
