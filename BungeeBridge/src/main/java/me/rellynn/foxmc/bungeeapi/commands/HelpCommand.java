package me.rellynn.foxmc.bungeeapi.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by gwennaelguich on 20/04/2017.
 * FoxMC Network.
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", null, "?");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText("§9---------- §e§lFOXMC Help §9----------"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/lobby §8- §7teleport you to lobby"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/msg <player> <message> §8- §7send a PM to player"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/reply <message> §8- §7reply to the last PM received"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/friend help §8- §7show help about friends"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/party help §8- §7show help about parties"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/report <player> <reason> §8- §7report a player to moderators"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/lag §8- §7show your ping and lag informations"));
        sender.sendMessage(TextComponent.fromLegacyText("§6» §e/help §8- §7show this help text"));
    }
}
