package me.rellynn.foxmc.bungeeapi.players;

import me.rellynn.foxmc.bungeeapi.commands.DefaultCommand;
import me.rellynn.foxmc.bungeeapi.utils.StringUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by gwennaelguich on 08/05/2017.
 * FoxMC Network.
 */
public class ReplyCommand extends DefaultCommand {

    ReplyCommand() {
        super("reply", "r", "rep");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cYou must be a player!"));
            return;
        } else if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText("§cUsage: §e/reply <message>"));
            return;
        }
        MessageCommand.reply((ProxiedPlayer) sender, StringUtils.join(args, " "));
    }
}
