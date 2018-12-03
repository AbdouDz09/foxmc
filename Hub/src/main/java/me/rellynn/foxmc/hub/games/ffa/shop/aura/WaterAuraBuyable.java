package me.rellynn.foxmc.hub.games.ffa.shop.aura;

import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableOnceEquippable;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.COINS;

/**
 * Created by gwennaelguich on 19/08/2017.
 * FoxMC Network.
 */
public class WaterAuraBuyable extends ItemBuyableOnceEquippable {

    WaterAuraBuyable() {
        super("ffa.aura", Effect.WATERDRIP.getName());
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 750;
        ItemStack icon = new ItemStack(Material.WATER_BUCKET);
        levels.put(0, new LevelInfoBuyable(COINS, 0, "§c* §lNEW §c* §rWater Aura", Arrays.asList("§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, "§rWater Aura", Arrays.asList("§aYou own this aura"), icon));
        return levels;
    }
}
