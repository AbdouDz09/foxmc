package me.rellynn.foxmc.bungeeapi.moderation.commands;

import me.rellynn.foxmc.api.moderation.packets.ModerationMessagePacket;
import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.commands.DefaultCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * Created by gwennaelguich on 15/05/2017.
 * FoxMC Network.
 */
public abstract class ModerationCommand extends DefaultCommand {
    public static final String PREFIX = "§7[§cMODCHAN§7] §r";
    private Rank rank;

    ModerationCommand(String name, Rank rank, String... aliases) {
        super(name, aliases);
        this.rank = rank;
    }

    void sendModMessage(BaseComponent... components) {
        FProtocolManager.get().broadcast(new ModerationMessagePacket(components));
    }

    boolean canSanction(ProxiedPlayer player, UUID target) {
        return FoxAPI.getPlayer(player).getRank().isHigherThan(FoxAPI.get().getPlayersHandler().getPlayer(target).getRank());
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && !FoxAPI.getPlayer((ProxiedPlayer) sender).isAtLeast(rank)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cPlease use §e/report <player> <reason>"));
        } else {
            onCommand(sender, args);
        }
    }
}
