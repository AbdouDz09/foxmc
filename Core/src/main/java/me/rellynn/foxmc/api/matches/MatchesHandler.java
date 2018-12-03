package me.rellynn.foxmc.api.matches;

import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.matches.packets.*;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.servers.ServerData;
import me.rellynn.foxmc.api.servers.ServersHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public abstract class MatchesHandler implements FPacketHandler {
    private ServersHandler serversHandler = CoreAPI.get().getServersHandler();
    private Set<MatchData> matches = new HashSet<>();

    protected MatchesHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    public Set<MatchData> filterMatches(String filter) {
        return matches.stream().filter(matchData -> matchData.hasFilter(filter)).collect(Collectors.toSet());
    }

    public Set<MatchData> filterMatches(Collection<String> filters) {
        return matches.stream().filter(matchData -> matchData.hasFilters(filters)).collect(Collectors.toSet());
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof MatchCreatePacket) {
            handleCreate((MatchCreatePacket) packet);
        } else if (packet instanceof MatchEndPacket) {
            handleEnd((MatchEndPacket) packet);
        } else if (packet instanceof MatchChangeStatePacket) {
            handleChangeState((MatchChangeStatePacket) packet);
        } else if (packet instanceof MatchUpdatePlayersPacket) {
            handleUpdatePlayers((MatchUpdatePlayersPacket) packet);
        } else if (packet instanceof JoinMatchRequestPacket) {
            handleJoinRequest((JoinMatchRequestPacket) packet);
        }
    }

    private void handleCreate(MatchCreatePacket packet) {
        ServerData server = serversHandler.getData(packet.getServerName());
        if (server != null) {
            MatchData data = new MatchData(packet.getId(), server, packet.getGame(), packet.getMap(), packet.getState(), 0, packet.getMaxPlayers(), false, packet.getFilters());
            if (server.addMatch(data)) {
                matches.add(data);
                onMatchCreate(data);
            }
        }
    }

    private void handleEnd(MatchEndPacket packet) {
        ServerData server = serversHandler.getData(packet.getServerName());
        if (server != null) {
            MatchData data = server.removeMatch(packet.getId());
            if (data != null) {
                matches.remove(data);
                onMatchEnd(data);
            }
        }
    }

    private void handleChangeState(MatchChangeStatePacket packet) {
        ServerData server = serversHandler.getData(packet.getServerName());
        if (server != null) {
            MatchData data = server.getMatch(packet.getId());
            if (data != null) {
                onMatchChangeState(data, packet.getNewState(), packet.isJoinable());
                data.setState(packet.getNewState());
                data.setJoinable(packet.isJoinable());
            }
        }
    }

    private void handleUpdatePlayers(MatchUpdatePlayersPacket packet) {
        ServerData server = serversHandler.getData(packet.getServerName());
        if (server != null) {
            MatchData data = server.getMatch(packet.getId());
            if (data != null) {
                onMatchUpdatePlayers(data, packet.getNewPlayers());
                data.setPlayers(packet.getNewPlayers());
            }
        }
    }

    /*
    Hooks
     */
    protected void onMatchCreate(MatchData match) {}

    protected void onMatchEnd(MatchData match) {}

    protected void onMatchChangeState(MatchData match, String newState, boolean newJoinable) {}

    protected void onMatchUpdatePlayers(MatchData match, int newPlayers) {}

    protected void handleJoinRequest(JoinMatchRequestPacket packet) {}
}
