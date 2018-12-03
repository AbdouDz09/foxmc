package me.rellynn.foxmc.api.database;

import me.rellynn.foxmc.api.CoreAPI;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

/**
 * Created by gwennaelguich on 25/04/2017.
 * FoxMC Network.
 */
public class RedisScript {
    private static final CoreAPI coreAPI = CoreAPI.get();

    private String plain;
    private String hash;

    RedisScript(String plain) {
        this.plain = plain;
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            this.hash = jedis.scriptLoad(plain);
        }
    }

    public Object eval(List<String> keys, List<String> args) {
        Object data;
        try (Jedis jedis = coreAPI.getRedisManager().getResource()) {
            try {
                data = jedis.evalsha(hash, keys, args);
            } catch (JedisException ex) {
                if (ex.getMessage().startsWith("NOSCRIPT")) {
                    data = jedis.eval(plain, keys, args);
                } else {
                    throw ex;
                }
            }
        }
        return data;
    }
}
