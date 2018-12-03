package me.rellynn.foxmc.bungeeapi;

import com.google.common.io.ByteStreams;
import me.rellynn.foxmc.api.database.RedisManager;
import me.rellynn.foxmc.api.database.SQLManager;
import me.rellynn.foxmc.bungeeapi.api.FoxAPI;
import me.rellynn.foxmc.bungeeapi.scheduler.RefreshProxyOptions;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 03/04/2017.
 * FoxMC Network.
 */
public class BridgePlugin extends Plugin {
    private static BridgePlugin plugin;

    public static BridgePlugin get() {
        return plugin;
    }

    private Configuration loadConfig(String name) throws IOException {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            throw new IOException("Unable to create plugin folder!");
        }
        File file = new File(getDataFolder(), name);
        if (file.createNewFile()) {
            try (InputStream in = getResourceAsStream(name); OutputStream out = new FileOutputStream(file)) {
                ByteStreams.copy(in, out);
            }
        }
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    @Override
    public void onEnable() {
        plugin = this;
        Configuration config;
        try {
            config = loadConfig("config.yml");
        } catch (IOException ex) {
            getLogger().severe("Unable to load configuration:");
            ex.printStackTrace();
            getLogger().severe("Stopping proxy...");
            getProxy().stop();
            return;
        }
        String proxyName = config.getString("proxyName", "");
        if (proxyName.isEmpty()) {
            getLogger().severe("Unable to start the server without a proxy name.");
            getProxy().stop();
            return;
        }
        SQLManager sqlManager = new SQLManager(config.getString("database.url"), config.getString("database.user"), config.getString("database.password"), 10);
        RedisManager redisManager = new RedisManager(config.getString("redis.host"));
        new FoxAPI(proxyName, sqlManager, redisManager);
        PluginRegistry.registerEverything();
        getProxy().getScheduler().schedule(this, new RefreshProxyOptions(), 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        FoxAPI.get().getRedisManager().killConnection();
    }
}
