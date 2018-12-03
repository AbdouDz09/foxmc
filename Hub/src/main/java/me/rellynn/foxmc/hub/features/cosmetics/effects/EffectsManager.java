package me.rellynn.foxmc.hub.features.cosmetics.effects;

import me.rellynn.foxmc.hub.HubPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class EffectsManager implements Listener {
    private Map<Player, Effect> playerEffects = new WeakHashMap<>();

    public EffectsManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, HubPlugin.get());
    }

    public void setEffect(Player player, Effect effect) {
        stopEffect(player);
        playerEffects.put(player, effect);
        effect.enable(player);
    }

    public boolean stopEffect(Player player) {
        Effect effect = playerEffects.remove(player);
        if (effect != null) {
            effect.disable(player);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        stopEffect(evt.getPlayer());
    }
}
