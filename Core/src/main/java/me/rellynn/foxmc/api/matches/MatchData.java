package me.rellynn.foxmc.api.matches;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.rellynn.foxmc.api.servers.ServerData;

import java.util.Collection;
import java.util.Set;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
@Data
@AllArgsConstructor
public class MatchData {
    private int id;
    private transient ServerData server;
    private String game;
    private String map;
    private String state;
    private int players;
    private int maxPlayers;
    private boolean joinable;
    private Set<String> filters;

    public String getName() {
        return server.getName() + "-" + id;
    }

    public boolean hasFilter(String filter) {
        return filters.contains(filter);
    }

    public boolean hasFilters(Collection<String> filters) {
        return this.filters.containsAll(filters);
    }
}
