package me.rellynn.foxmc.bungeeapi.parties;

import me.rellynn.foxmc.api.parties.PartiesHandler;
import me.rellynn.foxmc.api.parties.packets.PartyMessagePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerInvitePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerKickPacket;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static me.rellynn.foxmc.bungeeapi.parties.PartyCommand.PREFIX;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
public class PartiesManager extends PartiesHandler {

    public PartiesManager() {
        ProxyServer.getInstance().getPluginManager().registerCommand(BridgePlugin.get(), new PartyCommand(this));
    }

    @Override
    protected void handlePlayerInvite(PartyPlayerInvitePacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getTarget());
        if (player != null) {
            player.sendMessage(TextComponent.fromLegacyText(PREFIX + packet.getLeader() + " §7invited you to join a party!"));
            player.sendMessage(new ComponentBuilder("")
                    .append("[ACCEPT]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + packet.getParty())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to join party").create()))
                    .append(" or ").reset().color(ChatColor.GRAY)
                    .append("[DECLINE]").reset().color(ChatColor.RED).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party decline " + packet.getParty())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick to decline invitation").create()))
                    .create());
        }
    }

    @Override
    protected void handleMessage(PartyMessagePacket packet) {
        getMembers(packet.getParty()).forEach(uuid -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(packet.getMessage());
            }
        });
    }

    @Override
    protected void handlePlayerKick(PartyPlayerKickPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            player.sendMessage(TextComponent.fromLegacyText(PREFIX + "§e" + packet.getLeader() + " §ckicked you from the party!"));
        }
        BaseComponent[] message = TextComponent.fromLegacyText(PREFIX + packet.getPlayerName() + " §7has been kicked from the party!");
        getMembers(packet.getParty()).forEach(uuid -> {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(uuid);
            if (member != null) {
                member.sendMessage(message);
            }
        });
    }
}


