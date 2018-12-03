package me.rellynn.foxmc.gameapiv2.arenas;

import lombok.Getter;
import lombok.Setter;
import me.rellynn.foxmc.gameapiv2.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
@Getter
public class Arena {
    private String id;
    @Setter private String mode;
    @Setter private String map;
    @Setter private int minPlayers, maxPlayers;
    @Setter private boolean available = true;
    private Map<String, Location> points = new HashMap<>();
    private Set<Team> teams = new HashSet<>();
    private Cuboid cuboid;

    Arena(String id, String mode, String map, int minPlayers, int maxPlayers) {
        this.id = id;
        this.mode = mode;
        this.map = map;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Retrieve a point by its name.
     *
     * @param name The point name
     * @return The location
     */
    public Location getPoint(String name) {
        Location location = points.get(name);
        return location != null ? location.clone() : null;
    }

    /**
     * Set a point.
     * If the point is already created it will be replaced with new location.
     *
     * @param name     The point name
     * @param location The location
     */
    public void setPoint(String name, Location location) {
        points.put(name, location);
    }

    /**
     * Remove a point.
     *
     * @param name The point name
     * @return The boolean result, true = removed
     */
    public boolean removePoint(String name) {
        return points.remove(name) != null;
    }

    public Cuboid getCuboid() {
        if (cuboid == null) {
            Location border1 = getPoint("border.point1");
            Location border2 = getPoint("border.point2");
            if (border1 != null && border2 != null && border1.getWorld() == border2.getWorld())
                cuboid = new Cuboid(border1, border2);
        }
        return cuboid;
    }

    /**
     * Create and register a new team.
     *
     * @param id     The team id
     * @param prefix The scoreboard team prefix (showed in tablist)
     * @param name   The team name
     * @return The new {@link Team} instance
     */
    public Team registerTeam(String id, String prefix, String name) {
        Team team = new Team(id, prefix, name);
        teams.add(team);
        return team;
    }

    /**
     * Retrieve a team by its id.
     *
     * @param id The team id
     * @return The team or null
     */
    public Team getTeam(String id) {
        return teams.stream().filter(team -> team.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Retrieve a player team.
     *
     * @param player The player
     * @return The player team or null
     */
    public Team getPlayerTeam(Player player) {
        return teams.stream().filter(team -> team.hasPlayer(player)).findFirst().orElse(null);
    }

    public boolean hasTeam(Player player) {
        return teams.stream().anyMatch(team -> team.hasPlayer(player));
    }

    public Team getBestTeam() {
        Team bestTeam = null;
        for (Team team : teams) {
            if (bestTeam == null || team.getPlayers().size() < bestTeam.getPlayers().size())
                bestTeam = team;
        }
        return bestTeam;
    }
}
