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
public class TrapUpgrade extends Upgrade {
    public static final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 100, 0);
    public static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOW, 100, 0);

    public TrapUpgrade() {
        super("trap", new ItemStack(Material.TRAP_DOOR));
    }

    @Override
    protected Map<Integer, ShopLevel> buildLevels() {
        Map<Integer, ShopLevel> levels = new HashMap<>();
        levels.put(0, new ShopLevel(1, TWCurrency.BLUE_ORCHID, "§6It's a trap!", Arrays.asList("§7The next enemy to enter your", "§7base will receive Blindness", "§7and Slowness")));
        return levels;
    }
}
