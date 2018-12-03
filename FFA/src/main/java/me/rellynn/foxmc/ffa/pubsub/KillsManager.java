package me.rellynn.foxmc.ffa.pubsub;

import me.rellynn.foxmc.api.pubsub.PubSubReceiver;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.ffa.FFAPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gwennaelguich on 13/06/2017.
 * FoxMC Network.
 */
public class KillsManager extends BukkitRunnable implements PubSubReceiver {
    private Map<UUID, Map<UUID, Long>> kills = new ConcurrentHashMap<>();

    public KillsManager() {
        FoxAPI.get().getPubSub().subscribe("ffa.kills", this);
        runTaskTimerAsynchronously(FFAPlugin.get(), 1200, 1200); // Remove old entries each minute
    }

    public boolean hasKilled(Player killer, Player victim) {
        Map<UUID, Long> victims = kills.get(killer.getUniqueId());
        return victims != null && System.currentTimeMillis() < victims.getOrDefault(victim.getUniqueId(), 0L);
    }

    public void notifyKill(Player killer, Player victim) {
        long until = System.currentTimeMillis() + 600000; // 10 minutes
        FoxAPI.get().getPubSub().send("ffa.kills", killer.getUniqueId() + " " + victim.getUniqueId() + " " + until);
    }

    @Override
    public void receive(String channel, String message) {
        String[] data = message.split(" ");
        UUID killer = UUID.fromString(data[0]);
        UUID victim = UUID.fromString(data[1]);
        long until = Long.parseLong(data[2]);
        kills.computeIfAbsent(killer, uuid -> new ConcurrentHashMap<>()).put(victim, until);
    }

    @Override
    public void run() {
        kills.keySet().forEach(uuid -> {
            Set<Map.Entry<UUID, Long>> entrySet = kills.get(uuid).entrySet();
            entrySet.removeIf(entry -> System.currentTimeMillis() >= entry.getValue());
            if (entrySet.isEmpty()) {
                kills.remove(uuid);
            }
        });
    }
}
