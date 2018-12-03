package me.rellynn.foxmc.bukkitapi.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * Created by gwennaelguich on 14/04/2017.
 * FoxMC Network.
 */
public class PacketUtils {

    private static IChatBaseComponent toChatComponent(String text) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
    }

    private static PlayerConnection getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }

    /**
     * Send a NMS packet to all online players.
     *
     * @param packet The NMS packet
     */
    public static void broadcastPacket(Packet packet) {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> sendPacket(player, packet));
    }

    /**
     * Send a NMS packet to a player.
     *
     * @param player The player
     * @param packet The NMS packet
     */
    public static void sendPacket(Player player, Packet packet) {
        getPlayer(player).sendPacket(packet);
    }

    /**
     * Send a text in the action bar of a player.
     *
     * @param text   The text
     * @param player The player
     */
    public static void sendActionBar(String text, Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(toChatComponent(text), (byte) 2);
        sendPacket(player, packet);
    }

    private static void sendTitleTimes(Player player) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, 10, 30, 10);
        sendPacket(player, packet);
    }

    /**
     * Render a big text on the player screen.
     *
     * @param title  The text
     * @param player The player
     */
    public static void sendTitle(String title, Player player) {
        sendTitleTimes(player);
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, toChatComponent(title));
        sendPacket(player, packet);
    }

    /**
     * Render a text on the player screen.
     * You also need to send a title to show the sub title.
     *
     * @param title  The text
     * @param player The player
     */
    public static void sendSubTitle(String title, Player player) {
        sendTitleTimes(player);
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, toChatComponent(title));
        sendPacket(player, packet);
    }

    /**
     * Send a player list header and footer.
     *
     * @param header The header text
     * @param footer The footer text
     * @param player The player
     */
    public static void sendPlayerListHeaderFooter(String header, String footer, Player player) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(toChatComponent(header));
        try {
            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, toChatComponent(footer));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        sendPacket(player, packet);
    }
}
