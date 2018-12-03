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
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public class FlameAuraBuyable extends ItemBuyableOnceEquippable {

    FlameAuraBuyable() {
        super("ffa.aura", Effect.FLAME.getName());
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 750;
        String title = "§rFlame Aura";
        ItemStack icon = new ItemStack(Material.FLINT_AND_STEEL);
        levels.put(0, new LevelInfoBuyable(COINS, 0, title, Arrays.asList("§7I'm on FIRE!!!", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, title, Arrays.asList("§aYou own this aura"), icon));
        return levels;
    }
}
