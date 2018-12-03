package me.rellynn.foxmc.api.servers;

import lombok.Data;
import me.rellynn.foxmc.api.matches.MatchData;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 12/08/2017.
 * FoxMC Network.
 */
@Data
public class ServerData {
    private String name;
    private String host;
    private int port;
    private int players;
    private int maxPlayers;
    private ServerState state;
    private long lastHeartbeat;
    private Set<MatchData> matches = new HashSet<>();

    public ServerData(String name, String host, int port, int maxPlayers, ServerState state) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.state = state;
        heartbeat();
    }

    public InetSocketAddress getAddress() {
        return new InetSocketAddress(host, port);
    }

    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isUp() {
        return System.currentTimeMillis() - lastHeartbeat < 60000L;
    }

    public void addPlayers(int amount) {
        players += amount;
    }

    public boolean addMatch(MatchData data) {
        return matches.add(data);
    }

    public MatchData getMatch(int id) {
        return matches.stream().filter(data1 -> data1.getId() == id).findFirst().orElse(null);
    }

    public MatchData removeMatch(int id) {
        MatchData data = getMatch(id);
        if (data != null) {
            matches.remove(data);
        }
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServerData)) {
            return false;
        }
        ServerData data = (ServerData) obj;
        return data.getName().equals(name) && data.getHost().equals(host) && data.getPort() == port;
    }
}
