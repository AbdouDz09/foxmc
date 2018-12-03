package me.rellynn.foxmc.api.parties;

import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.database.RedisScript;
import me.rellynn.foxmc.api.parties.packets.PartyMessagePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerInvitePacket;
import me.rellynn.foxmc.api.parties.packets.PartyPlayerKickPacket;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;
import me.rellynn.foxmc.api.utils.IOUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by gwennaelguich on 16/08/2017.
 * FoxMC Network.
 */
public abstract class PartiesHandler implements FPacketHandler {
    private CoreAPI coreAPI = CoreAPI.get();
    private RedisScript getPlayerParty = coreAPI.getRedisManager().loadScript(IOUtils.readInputStreamAsString(coreAPI.getClass().getClassLoader().getResourceAsStream("lua/get_player_party.lua")));

    public PartiesHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    public void disbandParty(UUID party) {
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            jedis.keys("party:" + party + ":*").forEach(jedis::del);
        }
    }

    public UUID getPlayerParty(UUID player) {
        String result = (String) getPlayerParty.eval(new ArrayList<>(), Arrays.asList(player + ""));
        return result == null ? null : UUID.fromString(result);
    }

    public UUID getLeader(UUID party) {
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            String leader = jedis.get("party:" + party + ":leader");
            return leader == null ? null : UUID.fromString(leader);
        }
    }

    public boolean isLeader(UUID party, UUID player) {
        return player.equals(getLeader(party));
    }

    public Set<UUID> getMembers(UUID party) {
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            Set<UUID> players = new HashSet<>();
            Set<String> data = jedis.smembers("party:" + party + ":members");
            if (data != null) {
                data.forEach(uuid -> players.add(UUID.fromString(uuid)));
            }
            return players;
        }
    }

    public boolean hasPlayer(UUID party, UUID player) {
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            return jedis.sismember("party:" + party + ":members", player + "");
        }
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof PartyPlayerInvitePacket) {
            handlePlayerInvite((PartyPlayerInvitePacket) packet);
        } else if (packet instanceof PartyMessagePacket) {
            handleMessage((PartyMessagePacket) packet);
        } else if (packet instanceof PartyPlayerKickPacket) {
            handlePlayerKick((PartyPlayerKickPacket) packet);
        }
    }

    protected void handlePlayerInvite(PartyPlayerInvitePacket packet) {}

    protected void handleMessage(PartyMessagePacket packet) {}

    protected void handlePlayerKick(PartyPlayerKickPacket packet) {}
}
