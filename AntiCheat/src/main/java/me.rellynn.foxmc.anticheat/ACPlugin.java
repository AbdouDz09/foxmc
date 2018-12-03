package me.rellynn.foxmc.anticheat;

import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.data.MovingData;
import me.rellynn.foxmc.anticheat.listeners.*;
import me.rellynn.foxmc.anticheat.tasks.TickPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by gwennaelguich on 16/07/2017.
 * FoxMC Network.
 */
public class ACPlugin extends JavaPlugin {
    private static ACPlugin instance;

    public static ACPlugin get() {
        return instance;
    }

    public void initPlayer(Player player) {
        ACPlayer acPlayer = ACPlayer.get(player);
        MovingData movingData = acPlayer.movingData;
        movingData.updateMovingData(player.getLocation(), true);
        new TickPlayer(player, acPlayer, movingData);
    }

    @Override
    public void onEnable() {
        instance = this;
        new PacketListener();
        register(new MoveListener());
        register(new PlayerListener());
        register(new CombatListener());
        register(new BlockListener());
        register(new InventoryListener());
        getServer().getOnlinePlayers().forEach(this::initPlayer);
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
