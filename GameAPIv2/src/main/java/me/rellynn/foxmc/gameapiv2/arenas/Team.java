package me.rellynn.foxmc.gameapiv2.arenas;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.VirtualTeam;
import me.rellynn.foxmc.gameapiv2.games.Game;
import me.rellynn.foxmc.gameapiv2.utils.BroadcastType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class Team {
    @Getter private String id;
    @Getter private String name;
    @Getter private String prefix;
    @Getter private Set<UUID> players = new HashSet<>();
    @Getter private Map<String, Location> points = new HashMap<>();
    // Scoreboard Team
    @Getter private VirtualTeam virtualTeam;

    Team(String id, String prefix, String name) {
        this.id = id;
        this.prefix = prefix;
        this.name = name;
        this.virtualTeam = new VirtualTeam(name);
        this.virtualTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public void setPoint(String name, Location location) {
        points.put(name, location);
    }

    public Location getPoint(String name) {
        Location location = points.get(name);
        return location != null ? location.clone() : null;
    }

    public void removePoint(String name) {
        points.remove(name);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);
    }

    public void join(Game game, Player player) {
        addPlayer(player.getUniqueId());
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', prefix) + player.getName());
        game.broadcast(BroadcastType.PACKET, virtualTeam.addPlayer(player.getName()));
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public void leave(Game game, Player player) {
        removePlayer(player.getUniqueId());
        player.setDisplayName(FoxAPI.getPlayer(player).getDisplayName());
        game.broadcast(BroadcastType.PACKET, virtualTeam.removePlayer(player.getName()));
    }

    public void clear() {
        players.clear();
    }

    /**
     * @return The team online players
     */
    public Set<Player> getOnlinePlayers(Game game) {
        Set<Player> players = new HashSet<>();
        this.players.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && game.isPlayer(player))
                players.add(player);
        });
        return players;
    }
}
