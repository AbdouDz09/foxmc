package me.rellynn.foxmc.bungeeapi.network;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class SetOptionCommand extends Command {
    // Default options
    public static String motd = "FoxMC";
    public static int maxPlayers = 100;
    public static boolean isOpened = false;

    public SetOptionCommand() {
        super("setoption");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !FoxAPI.getPlayer((ProxiedPlayer) sender).isAtLeast(Rank.OWNER)) {
            return;
        } else if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: /setoption <option> <value>"));
            return;
        }
        String option = args[0];
        String value = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        switch (option) {
            case "motd":
                motd = value;
                sender.sendMessage(TextComponent.fromLegacyText("§aYou changed the server description!"));
                break;
            case "maxplayers":
                if (!value.matches("[0-9]+")) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cThe max players must be an integer!"));
                    return;
                }
                maxPlayers = Integer.parseInt(value);
                sender.sendMessage(TextComponent.fromLegacyText("§aYou changed the max players!"));
                break;
            case "status":
                if (!value.equalsIgnoreCase("opened") && !value.equalsIgnoreCase("closed")) {
                    sender.sendMessage(TextComponent.fromLegacyText("§cThe status must be opened or closed."));
                    return;
                }
                value = value.toLowerCase();
                isOpened = value.equalsIgnoreCase("opened");
                sender.sendMessage(TextComponent.fromLegacyText("§aServer status set to " + value));
                break;
            default:
                sender.sendMessage(TextComponent.fromLegacyText("§cThe option you specified doesn't exist."));
                return;
        }
        try (Jedis jedis = FoxAPI.get().getRedisManager().getResource()) {
            jedis.set("option:" + option, value);
        }
    }
}
