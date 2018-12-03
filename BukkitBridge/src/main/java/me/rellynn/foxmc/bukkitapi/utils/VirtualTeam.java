package me.rellynn.foxmc.bukkitapi.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gwennaelguich on 13/05/2017.
 * FoxMC Network.
 */
public class VirtualTeam {
    private String name;
    @Getter @Setter private String prefix = "";
    @Getter @Setter private String suffix = "";
    @Getter private List<String> players = new ArrayList<>();

    public VirtualTeam(String name) {
        this.name = name;
    }

    private PacketPlayOutScoreboardTeam createPacket(int mode, List<String> players) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        Scoreboard.setField(packet, "a", name);
        Scoreboard.setField(packet, "h", mode);
        switch (mode) {
            case 0:
                Scoreboard.setField(packet, "b", "");
                Scoreboard.setField(packet, "c", prefix);
                Scoreboard.setField(packet, "d", suffix);
                Scoreboard.setField(packet, "i", 0);
                Scoreboard.setField(packet, "e", "always");
                Scoreboard.setField(packet, "f", 0);
                Scoreboard.setField(packet, "g", players);
                break;
            case 2:
                Scoreboard.setField(packet, "c", prefix);
                Scoreboard.setField(packet, "d", suffix);
                break;
            case 3:
            case 4:
                Scoreboard.setField(packet, "g", players);
                break;
        }
        return packet;
    }

    public PacketPlayOutScoreboardTeam create(String... newPlayers) {
        if (newPlayers != null) {
            players.addAll(Arrays.asList(newPlayers));
        }
        return createPacket(0, players);
    }

    public PacketPlayOutScoreboardTeam update() {
        return createPacket(2, null);
    }

    public PacketPlayOutScoreboardTeam addPlayer(String player) {
        players.add(player);
        return createPacket(3, Arrays.asList(player));
    }

    public PacketPlayOutScoreboardTeam removePlayer(String player) {
        players.remove(player);
        return createPacket(4, Arrays.asList(player));
    }

    public PacketPlayOutScoreboardTeam destroy() {
        return createPacket(1, null);
    }
}
