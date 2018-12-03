package me.rellynn.foxmc.hub.games.ffa.shop.effects;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import me.rellynn.foxmc.hub.shops.items.ItemBuyableOnce;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.COINS;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public class MagicalRodEffectBuyable extends ItemBuyableOnce {

    MagicalRodEffectBuyable() {
        super("ffa.effects", "magical_rod");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 500;
        String title = "§eMagical Rod";
        ItemStack icon = new ItemBuilder(Material.FISHING_ROD).setGlow(true).build();
        levels.put(0, new LevelInfoBuyable(COINS, 0, title, Arrays.asList("§7Add magical particles when", "§7you cast your fishing rod!", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, title, Arrays.asList("§aYou own this effect"), icon));
        return levels;
    }
}
