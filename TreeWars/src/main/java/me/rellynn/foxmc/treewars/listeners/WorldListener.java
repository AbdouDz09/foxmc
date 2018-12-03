package me.rellynn.foxmc.treewars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class WorldListener implements Listener {

    /*
    Weather
     */
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent evt) {
        if (evt.toWeatherState())
            evt.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent evt) {
        if (evt.toThunderState())
            evt.setCancelled(true);
    }

    /*
    Sapling is a sapling, not a tree.
     */
    @EventHandler
    public void onStructureGrow(StructureGrowEvent evt) {
        evt.setCancelled(true);
    }
}
