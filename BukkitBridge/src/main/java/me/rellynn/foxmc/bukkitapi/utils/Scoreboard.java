package me.rellynn.foxmc.bukkitapi.utils;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by gwennaelguich on 17/04/2017.
 * FoxMC Network.
 */
public class Scoreboard {

    private static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Player player;
    private String title;
    private VirtualTeam[] teams = new VirtualTeam[15];
    @Getter private boolean destroyed;

    /**
     * Create a player scoreboard.
     * Use {@link Scoreboard#setLine(int, String)} to add lines.
     *
     * @param player The player
     * @param title  The scoreboard title
     */
    public Scoreboard(Player player, String title) {
        this.player = player;
        this.title = title;
        sendInitPackets();
    }

    private void sendInitPackets() {
        sendPacket(player, createObjectPacket(0));
        PacketPlayOutScoreboardDisplayObjective displayPacket = new PacketPlayOutScoreboardDisplayObjective();
        setField(displayPacket, "a", 1);
        setField(displayPacket, "b", player.getName());
        sendPacket(player, displayPacket);
    }

    private PacketPlayOutScoreboardObjective createObjectPacket(int mode) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        setField(packet, "a", player.getName());
        setField(packet, "d", mode);
        if (mode == 0) {
            setField(packet, "b", title);
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }
        return packet;
    }

    private String[] getParts(String value) {
        if (value.length() <= 16) {
            return new String[]{value, ""};
        }
        String prefix = value.substring(0, 16);
        String suffix = value.substring(16, value.length());
        int colorIndex = prefix.lastIndexOf('§');
        if (colorIndex != -1 && colorIndex + 1 < prefix.length()) {
            suffix = "§" + prefix.charAt(colorIndex + 1) + suffix;
        } else if (colorIndex == 15) {
            prefix = prefix.substring(0, 15);
            suffix = "§" + suffix;
        }
        return new String[]{prefix, suffix.substring(0, suffix.length() >= 16 ? 16 : suffix.length())};
    }

    /**
     * Set the text of a scoreboard line.
     *
     * @param line  The line number
     * @param value The text
     */
    public void setLine(int line, String value) {
        if (line >= 0 && line < 15) {
            VirtualTeam team = teams[line];
            if (team == null) {
                team = new VirtualTeam("_" + line);
                teams[line] = team;
                String playerDisplay = "" + ChatColor.values()[line] + ChatColor.RESET;
                sendPacket(player, team.create(playerDisplay));
                PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(playerDisplay);
                setField(packet, "b", player.getName());
                setField(packet, "c", 15 - line);
                setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
                sendPacket(player, packet);
            }
            String oldPrefix = team.getPrefix();
            String oldSuffix = team.getSuffix();
            String[] parts = getParts(value);
            String prefix = parts[0];
            String suffix = parts.length > 1 ? parts[1] : "";
            team.setPrefix(prefix);
            team.setSuffix(suffix);
            if (!prefix.equals(oldPrefix) || !suffix.equals(oldSuffix)) {
                sendPacket(player, team.update());
            }
        }
    }

    /**
     * Remove a line on the scoreboard.
     *
     * @param line The line number
     */
    public void removeLine(int line) {
        VirtualTeam team = teams[line];
        if (team != null) {
            sendPacket(player, new PacketPlayOutScoreboardScore(team.getPlayers().get(0)));
            sendPacket(player, team.destroy());
            teams[line] = null;
        }
    }

    /**
     * Destroy the player scoreboard.
     * It sends the packets to remove the teams and scores.
     */
    public void destroy() {
        sendPacket(player, createObjectPacket(1));
        for (VirtualTeam team : teams) {
            if (team != null) {
                sendPacket(player, team.destroy());
            }
        }
        destroyed = true;
    }
}
