package me.rellynn.foxmc.api.pubsub;

import me.rellynn.foxmc.api.CoreAPI;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class PubSubAPI {
    private Subscriber subscriber;
    private Sender sender;
    private boolean continueSub = true;

    public PubSubAPI() {
        this.subscriber = new Subscriber();
        new Thread(() -> {
            while (continueSub) {
                try (Jedis jedis = CoreAPI.get().getRedisManager().getJedisResource()) {
                    jedis.psubscribe(subscriber, "*");
                } catch (JedisException e) {
                    e.printStackTrace();
                }
                CoreAPI.get().getLogger().info("Disconnected from master.");
            }
        }, "FoxCore-RedisSub").start();
        CoreAPI.get().getLogger().info("Waiting for subscribing...");
        while (!subscriber.isSubscribed()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CoreAPI.get().getLogger().info("Correctly subscribed.");
        sender = new Sender(CoreAPI.get());
        new Thread(sender, "FoxCore-RedisPub").start();
    }

    /**
     * Subscribe to a Redis channel.
     *
     * @param pattern  The pattern to subscribe on
     * @param receiver A {@link PubSubReceiver} instance
     */
    public void subscribe(String pattern, PubSubReceiver receiver) {
        subscriber.registerPattern(pattern, receiver);
    }

    /**
     * Unsubscribe a Redis channel.
     *
     * @param pattern The pattern to unsubscribe
     */
    public void unsubscribe(String pattern) {
        subscriber.unsubscribePattern(pattern);
    }

    /**
     * Send a message through a Redis channel.
     *
     * @param channel The channel
     * @param message The message to send
     */
    public void send(String channel, String message) {
        sender.publish(channel, message);
    }
}
