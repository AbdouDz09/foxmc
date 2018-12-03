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
public class EndAuraBuyable extends ItemBuyableOnceEquippable {

    EndAuraBuyable() {
        super("ffa.aura", Effect.PORTAL.getName());
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 850;
        String title = "§rEnd Aura";
        ItemStack icon = new ItemStack(Material.EYE_OF_ENDER);
        levels.put(0, new LevelInfoBuyable(COINS, 0, title, Arrays.asList("§7Errr...", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, title, Arrays.asList("§aYou own this aura"), icon));
        return levels;
    }
}
