package me.rellynn.foxmc.api.players;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.rellynn.foxmc.api.CoreAPI;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.players.packets.*;
import me.rellynn.foxmc.api.protocol.FPacket;
import me.rellynn.foxmc.api.protocol.FPacketHandler;
import me.rellynn.foxmc.api.protocol.FProtocolManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 13/08/2017.
 * FoxMC Network.
 */
public abstract class PlayersHandler implements FPacketHandler {
    private Map<UUID, PlayerData> playersCache = new ConcurrentHashMap<>();
    private Cache<UUID, PlayerData> loadingCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(30, TimeUnit.SECONDS).build();

    protected PlayersHandler() {
        FProtocolManager.get().registerHandler(this);
    }

    private PlayerData loadPlayer(UUID uuid, boolean createIfNeeded) {
        PlayerData data = loadingCache.getIfPresent(uuid);
        if (data != null) {
            loadingCache.put(uuid, data); // Put it again to reset expire time
            return data;
        }
        try {
            PlayerData loadedData = CoreAPI.get().getSqlManager().execute(() -> {
                PlayerData playerData = PlayerData.findById(uuid + "");
                if (playerData == null && createIfNeeded) {
                    // Default values
                    playerData = new PlayerData()
                            .setId(uuid + "")
                            .set("coins", 0).set("tails", 0)
                            .set("shop", "{}").set("settings", "{}").set("stats", "{}").set("data", "{}")
                            .set("vip_level", 0).set("mod_level", 0)
                            .set("first_login", System.currentTimeMillis());
                }
                return playerData;
            }).get();
            // Prevent null values caching
            if (loadedData != null) {
                loadingCache.put(uuid, loadedData);
            }
            return loadedData;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayerData loadPlayer(UUID uuid) {
        return loadPlayer(uuid, true);
    }

    public PlayerData getPlayer(UUID uuid, boolean createIfNeeded) {
        PlayerData data = playersCache.get(uuid);
        if (data == null) {
            data = loadPlayer(uuid, createIfNeeded);
        }
        return data;
    }

    public PlayerData getPlayer(UUID uuid) {
        return getPlayer(uuid, true);
    }

    public PlayerData validate(UUID uuid) {
        PlayerData data = loadingCache.getIfPresent(uuid);
        if (data != null) {
            playersCache.put(uuid, data);
        }
        return data;
    }

    public PlayerData unload(UUID uuid) {
        loadingCache.invalidate(uuid);
        return playersCache.remove(uuid);
    }

    @Override
    public void handle(FPacket packet) {
        if (packet instanceof PlayerConnectPacket) {
            handleConnect((PlayerConnectPacket) packet);
        } else if (packet instanceof PlayerMessagePacket) {
            handleMessage((PlayerMessagePacket) packet);
        } else if (packet instanceof PlayerSetRankPacket) {
            handleSetRank((PlayerSetRankPacket) packet);
        } else if (packet instanceof PlayerNickChangePacket) {
            handleNickChange((PlayerNickChangePacket) packet);
        } else if (packet instanceof PlayerSetAmountPacket) {
            handleSetAmount((PlayerSetAmountPacket) packet);
        } else if (packet instanceof PlayerSetDataPacket) {
            handleSetData((PlayerSetDataPacket) packet);
        } else if (packet instanceof PlayerVotePacket) {
            handleVote((PlayerVotePacket) packet);
        }
    }

    private void handleSetRank(PlayerSetRankPacket packet) {
        PlayerData data = playersCache.get(packet.getPlayer());
        if (data != null) {
            Rank rank = packet.getRank();
            if (!rank.isMod()) {
                data.set("mod_level", 0);
            }
            data.set(rank.isMod() ? "mod_level" : "vip_level", rank.getLevel());
            onSetRank(packet.getPlayer(), rank);
        }
    }

    private void handleNickChange(PlayerNickChangePacket packet) {
        PlayerData data = playersCache.get(packet.getPlayer());
        if (data != null) {
            data.set("nickname", packet.getNickName());
            onNickChange(packet.getPlayer(), packet.getNickName());
        }
    }

    private void handleSetAmount(PlayerSetAmountPacket packet) {
        PlayerData data = playersCache.get(packet.getPlayer());
        if (data != null) {
            data.set(packet.getCurrency(), packet.getAmount());
        }
    }

    private void handleSetData(PlayerSetDataPacket packet) {
        PlayerData data = playersCache.get(packet.getPlayer());
        if (data != null) {
            data.set(packet.getKey(), packet.getValue());
        }
    }

    private void handleVote(PlayerVotePacket packet) {
        PlayerData data = playersCache.get(packet.getPlayer());
        if (data != null) {
            data.set("last_vote", packet.getDate());
            onVote(packet.getPlayer());
        }
    }

    protected void handleConnect(PlayerConnectPacket packet) {}

    protected void handleMessage(PlayerMessagePacket packet) {}

    protected void onSetRank(UUID uuid, Rank rank) {}

    protected void onNickChange(UUID uuid, String nickName) {}

    protected void onVote(UUID uuid) {}
}
