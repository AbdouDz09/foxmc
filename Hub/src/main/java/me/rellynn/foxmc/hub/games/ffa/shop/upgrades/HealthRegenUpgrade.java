package me.rellynn.foxmc.hub.games.ffa.shop.upgrades;

import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableUpgradable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.COINS;

/**
 * Created by gwennaelguich on 19/06/2017.
 * FoxMC Network.
 */
public class HealthRegenUpgrade extends ItemBuyableUpgradable {

    HealthRegenUpgrade() {
        super("ffa.upgrades", "health", new LevelInfoBuyable(COINS, 0, "§6Health Regen §e0", Arrays.asList("§7Gives you health when you", "§7kill someone", "§7§oWorks on Classic & OP", "", "§cNothing"), new ItemStack(Material.APPLE)));
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(COINS, 500, "§6Health Regen §e1", Arrays.asList("§7You get §8+§c3 ❤", "", "§aRight click: unlock §e500 FC"), new ItemStack(Material.APPLE)));
        levels.put(1, new LevelInfoBuyable(COINS, 750, "§6Health Regen §e2", Arrays.asList("§7You get §8+§c4 ❤", "", "§aRight click: unlock §e750 FC"), new ItemStack(Material.APPLE, 2)));
        levels.put(2, new LevelInfoBuyable(COINS, 1000, "§6Health Regen §e3", Arrays.asList("§7You get §8+§c5 ❤", "", "§aRight click: unlock §e1000 FC"), new ItemStack(Material.APPLE, 3)));
        levels.put(3, new LevelInfoBuyable(COINS, 0, "§6Health Regen §e3", Arrays.asList("§7You have §8+§c5 ❤", "§cNo more upgrades", ""), new ItemStack(Material.APPLE, 3)));
        return levels;
    }
}
