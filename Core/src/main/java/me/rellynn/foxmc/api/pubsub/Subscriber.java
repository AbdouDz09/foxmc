package me.rellynn.foxmc.api.pubsub;

import me.rellynn.foxmc.api.CoreAPI;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class Subscriber extends JedisPubSub {
    private Map<String, Set<PubSubReceiver>> patterns;

    Subscriber() {
        patterns = new HashMap<>();
    }

    void registerPattern(String pattern, PubSubReceiver receiver) {
        patterns.computeIfAbsent(pattern, s -> new HashSet<>()).add(receiver);
        this.psubscribe(pattern);
    }

    void unsubscribePattern(String pattern) {
        patterns.remove(pattern);
        this.punsubscribe(pattern);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        Set<PubSubReceiver> receivers = patterns.get(pattern);
        if (receivers != null) {
            receivers.forEach(receiver -> {
                try {
                    receiver.receive(channel, message);
                } catch (Exception ex) {
                    CoreAPI.get().getLogger().severe("Exception while receiving message from redis:");
                    ex.printStackTrace();
                }
            });
        }
    }
}
