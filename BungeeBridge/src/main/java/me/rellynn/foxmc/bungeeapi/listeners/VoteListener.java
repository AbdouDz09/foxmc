package me.rellynn.foxmc.bungeeapi.listeners;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.players.packets.PlayerVotePacket;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.bungeeapi.BridgePlugin;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * Created by gwennaelguich on 27/05/2017.
 * FoxMC Network.
 */
public class VoteListener implements Listener {

    @EventHandler
    public void onVote(VotifierEvent evt) {
        String name = evt.getVote().getUsername();
        UUID uuid = FoxAPI.get().getUuidTranslator().getUUID(name, true);
        PlayerData data = uuid != null ? FoxAPI.get().getPlayersHandler().getPlayer(uuid, false) : null;
        if (data == null) {
            BridgePlugin.get().getLogger().warning("Unknown player tried to vote: " + name);
        } else if (!data.hasVotedToday()) {
            long date = System.currentTimeMillis();
            data.set("last_vote", date);
            data.increaseCoins(75, (balance, diff, err) -> {
                if (err != null) {
                    BridgePlugin.get().getLogger().severe("Unable to give vote reward to " + data.getName() + "!");
                    err.printStackTrace();
                    return;
                }
                FProtocolManager.get().broadcastServers(new PlayerVotePacket(uuid, date));
            });
        }
    }
}
