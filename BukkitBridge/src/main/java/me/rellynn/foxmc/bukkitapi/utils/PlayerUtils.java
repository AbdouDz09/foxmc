package me.rellynn.foxmc.bukkitapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by gwennaelguich on 10/04/2017.
 * FoxMC Network.
 */
public class PlayerUtils {

    /**
     * Reset a player.
     * It clear player inventory, reset health, set default gamemode,
     * remove potions effects, reset food level, saturation...
     *
     * @param player The player
     */
    public static void resetPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setItemOnCursor(null);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.setNoDamageTicks(0);
        player.setLastDamage(0);
        player.setLastDamageCause(null);
        player.resetMaxHealth();
        player.setHealth(player.getMaxHealth());
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(Bukkit.getDefaultGameMode());
        player.setExp(0);
        player.setLevel(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
