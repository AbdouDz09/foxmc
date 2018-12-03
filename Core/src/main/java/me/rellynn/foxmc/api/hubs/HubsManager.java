package me.rellynn.foxmc.api.hubs;

import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServerState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class HubsManager {
    private Set<ServerData> availableHubs = new HashSet<>();

    public List<ServerData> getHubs() {
        List<ServerData> hubs = new ArrayList<>(availableHubs.stream().filter(data -> data.getState() == ServerState.JOINABLE && HubLevel.getLevel(data).getWeight() > 0).collect(Collectors.toSet()));
        hubs.sort(Comparator.comparingInt(server -> HubLevel.getLevel((ServerData) server).getWeight()).reversed());
        return hubs;
    }

    public ServerData getBestHub() {
        List<ServerData> hubs = getHubs();
        if (hubs.isEmpty()) {
            return null;
        }
        return hubs.get(0);
    }

    public void addHubServer(ServerData data) {
        availableHubs.add(data);
    }

    public void removeHubServer(ServerData data) {
        availableHubs.remove(data);
    }
}
