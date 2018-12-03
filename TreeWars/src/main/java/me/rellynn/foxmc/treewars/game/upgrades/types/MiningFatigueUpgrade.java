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
public class MiningFatigueUpgrade extends Upgrade {
    public static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 0);

    public MiningFatigueUpgrade() {
        super("mining_fatigue", new ItemStack(Material.WOOD_PICKAXE));
    }

    @Override
    protected Map<Integer, ShopLevel> buildLevels() {
        Map<Integer, ShopLevel> levels = new HashMap<>();
        levels.put(0, new ShopLevel(5, TWCurrency.BLUE_ORCHID, "§6Mining Fatigue", Arrays.asList("§7Enemies gain Mining Fatigue I", "§7within a 5 blocks radius", "§7of your tree")));
        return levels;
    }
}
