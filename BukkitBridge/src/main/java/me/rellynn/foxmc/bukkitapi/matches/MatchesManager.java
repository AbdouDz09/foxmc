package me.rellynn.foxmc.bukkitapi.matches;

import me.rellynn.foxmc.api.matches.MatchData;
import me.rellynn.foxmc.api.matches.MatchesHandler;
import me.rellynn.foxmc.api.matches.packets.JoinMatchRequestPacket;
import me.rellynn.foxmc.api.players.packets.PlayerConnectPacket;
import me.rellynn.foxmc.api.players.packets.PlayerMessagePacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.events.PartyJoinMatchEvent;
import me.rellynn.foxmc.bukkitapi.events.PlayerJoinMatchEvent;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public class MatchesManager extends MatchesHandler {
    private FoxAPI foxAPI = FoxAPI.get();

    private void handlePlayerJoin(UUID player, MatchData match) {
        PlayerJoinMatchEvent evt = new PlayerJoinMatchEvent(player, match);
        Bukkit.getPluginManager().callEvent(evt);
        String serverName = foxAPI.getNetworkHandler().getServer(player);
        if (serverName != null) {
            if (evt.getCancelReason() != null) {
                FProtocolManager.get().sendToServer(serverName, new PlayerMessagePacket(player, evt.getCancelReason()));
                return;
            }
            FProtocolManager.get().sendToServer(serverName, new PlayerConnectPacket(player, match.getName()));
        }
    }

    private void handlePartyJoin(UUID player, MatchData match, UUID party) {
        if (!foxAPI.getPartiesHandler().isLeader(party, player)) {
            FProtocolManager.get().sendToServer(foxAPI.getNetworkHandler().getServer(player), new PlayerMessagePacket(player, "§cOnly the party leader can join games!"));
            return;
        }
        Map<UUID, String> onlineMembers = new HashMap<>();
        foxAPI.getPartiesHandler().getMembers(party).forEach(uuid -> {
            String serverName = foxAPI.getNetworkHandler().getServer(uuid);
            if (serverName != null) {
                onlineMembers.put(uuid, serverName);
            }
        });
        PartyJoinMatchEvent evt = new PartyJoinMatchEvent(player, match, party, onlineMembers.keySet());
        Bukkit.getPluginManager().callEvent(evt);
        if (evt.getCancelReason() != null) {
            FProtocolManager.get().sendToServer(onlineMembers.get(player), new PlayerMessagePacket(player, evt.getCancelReason()));
            return;
        }
        onlineMembers.forEach((uuid, serverName) -> FProtocolManager.get().sendToServer(serverName, new PlayerConnectPacket(uuid, match.getName())));
    }

    @Override
    protected void handleJoinRequest(JoinMatchRequestPacket packet) {
        MatchData data = foxAPI.getServerData().getMatch(packet.getId());
        if (data == null) {
            FProtocolManager.get().sendToServer(foxAPI.getNetworkHandler().getServer(packet.getPlayer()), new PlayerMessagePacket(packet.getPlayer(), "§cThe game doesn't exist!"));
            return;
        }
        UUID party = FoxAPI.get().getPartiesHandler().getPlayerParty(packet.getPlayer());
        if (party != null) {
            handlePartyJoin(packet.getPlayer(), data, party);
            return;
        }
        handlePlayerJoin(packet.getPlayer(), data);
    }
}
