package me.rellynn.foxmc.bungeeapi.anticheat;

import me.rellynn.foxmc.api.anticheat.AntiCheatHandler;
import me.rellynn.foxmc.api.anticheat.packets.ACViolationPacket;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
public class AntiCheatManager extends AntiCheatHandler {

    @Override
    protected void handleViolation(ACViolationPacket packet) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayer());
        if (player != null) {
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/modtp " + player.getName());
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eClick to TP!"));
            BaseComponent[] components = TextComponent.fromLegacyText("§7[§9ANTICHEAT§7] §e" + player.getName() + " §o(" + player.getServer().getInfo().getName() + ") §6failed §r" + packet.getCheckName() + " check §o(ping=" + packet.getPing() + "ms)");
            for (BaseComponent component : components) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }
            for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers()) {
                if (FoxAPI.getPlayer(online).getModLevel() > 0) {
                    online.sendMessage(components);
                }
            }
        }
    }
}
