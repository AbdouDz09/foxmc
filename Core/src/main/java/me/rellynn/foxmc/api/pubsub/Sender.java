package me.rellynn.foxmc.api.pubsub;

import me.rellynn.foxmc.api.CoreAPI;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gwennaelguich on 05/04/2017.
 * FoxMC Network.
 */
public class Sender implements Runnable {
    private CoreAPI coreAPI;
    private Jedis jedis;
    private LinkedBlockingQueue<PendingMessage> queue;

    Sender(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.queue = new LinkedBlockingQueue<>();
    }

    void publish(String channel, String message) {
        queue.add(new PendingMessage(channel, message));
    }

    @Override
    public void run() {
        fixJedis();
        while (true) {
            try {
                PendingMessage message = queue.take();
                boolean published = false;
                while (!published) {
                    try {
                        jedis.publish(message.channel, message.message);
                        published = true;
                    } catch (Exception ex) {
                        fixJedis();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                jedis.close();
                return;
            }
        }
    }

    private void fixJedis() {
        try {
            jedis = coreAPI.getRedisManager().getResource();
        } catch (JedisException ex) {
            coreAPI.getLogger().severe("[Publisher] Unable to connect to redis server: " + ex.getMessage() + ". Retrying in 5 seconds...");
            try {
                Thread.sleep(5000);
                fixJedis();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class PendingMessage {
        String channel;
        String message;

        PendingMessage(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }
}
