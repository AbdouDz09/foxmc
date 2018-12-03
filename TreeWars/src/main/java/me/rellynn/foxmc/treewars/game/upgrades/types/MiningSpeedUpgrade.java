package me.rellynn.foxmc.treewars.game.upgrades.types;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 01/06/2017.
 * FoxMC Network.
 */
public class MiningSpeedUpgrade extends Upgrade {
    public static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0);

    public MiningSpeedUpgrade() {
        super("mining_speed", new ItemStack(Material.GOLD_PICKAXE));
    }

    @Override
    protected Map<Integer, ShopLevel> buildLevels() {
        Map<Integer, ShopLevel> levels = new HashMap<>();
        levels.put(0, new ShopLevel(5, TWCurrency.BLUE_ORCHID, "§6Mining Speed", Arrays.asList("§7You and your teammates", "§7permanently gets Haste I")));
        return levels;
    }
}
