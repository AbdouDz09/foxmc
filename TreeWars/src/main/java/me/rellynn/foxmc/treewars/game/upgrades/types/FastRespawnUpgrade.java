package me.rellynn.foxmc.treewars.game.upgrades.types;

import me.rellynn.foxmc.treewars.game.TWCurrency;
import me.rellynn.foxmc.treewars.game.upgrades.Upgrade;
import me.rellynn.foxmc.treewars.shops.levels.ShopLevel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 11/06/2017.
 * FoxMC Network.
 */
public class FastRespawnUpgrade extends Upgrade {

    public FastRespawnUpgrade() {
        super("speed_respawn", new ItemStack(Material.SUGAR));
    }

    @Override
    protected Map<Integer, ShopLevel> buildLevels() {
        Map<Integer, ShopLevel> levels = new HashMap<>();
        levels.put(0, new ShopLevel(10, TWCurrency.BLUE_ORCHID, "§6Speed Respawn", Arrays.asList("§7Your team gets 3 seconds", "§7on death to respawn!")));
        return levels;
    }
}
