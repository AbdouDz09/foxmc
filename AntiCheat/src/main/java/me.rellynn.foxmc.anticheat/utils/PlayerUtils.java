package me.rellynn.foxmc.anticheat.utils;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class PlayerUtils {

    public static int getPotionLevel(Player player, PotionEffectType type) {
        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().equals(type)).map(potionEffect -> potionEffect.getAmplifier() + 1).findFirst().orElse(0);
    }

    public static boolean canSlowDown(Player player) {
        if (player.getItemInHand() == null) {
            return false;
        }
        Material type = player.getItemInHand().getType();
        boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        return type == Material.IRON_SWORD
                || type == Material.DIAMOND_SWORD
                || type == Material.WOOD_SWORD
                || type == Material.GOLD_SWORD
                || type == Material.STONE_SWORD
                || (type == Material.POTION && (player.getItemInHand().getDurability() & 16384) == 0)
                || (type == Material.BOW && (isCreative || player.getInventory().contains(Material.ARROW)))
                || (!isCreative && type.isEdible() && (type == Material.GOLDEN_APPLE || player.getFoodLevel() < 20));
    }
}
