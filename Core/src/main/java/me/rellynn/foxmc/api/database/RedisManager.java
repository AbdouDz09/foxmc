package me.rellynn.foxmc.api.database;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by gwennaelguich on 04/04/2017.
 * FoxMC Network.
 */
public class RedisManager {
    private String host;
    private JedisPool pool;

    public RedisManager(String host) {
        this.host = host;
        // CoreAPI.get().getLogger().info("[Redis] Initializing connection...");
        try {
            initiateConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initiateConnection() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        pool = new JedisPool(config, host, 6379);
        // CoreAPI.get().getLogger().info("[Redis] Connection initialized.");
    }

    /**
     * Retrieve a Jedis connection from the pool.
     * It allows the user to do operations in the redis cache.
     *
     * @return {@link Jedis} instance
     */
    public Jedis getResource() {
        return pool.getResource();
    }

    public Jedis getJedisResource() {
        return new Jedis(host, 6379);
    }

    /**
     * Load a lua script into the Redis connection.
     *
     * @param script The script content.
     * @return {@link RedisScript} instance
     */
    public RedisScript loadScript(String script) {
        return new RedisScript(script);
    }

    /**
     * Close all redis connections and destroy the pool.
     */
    public void killConnection() {
        pool.destroy();
    }
}
