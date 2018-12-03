package me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes;

import me.rellynn.foxmc.hub.features.cosmetics.effects.types.AroundHeadEffect;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class WaterCrownEffect extends AroundHeadEffect {

    public WaterCrownEffect() {
        super("water_crown", 2L);
        radius = 0.3D;
        angle = 22.5F;
        offset = 0.2D;
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 700;
        String title = "§6Water Crown";
        ItemStack icon = new ItemStack(Material.WATER_BUCKET);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Like a King but with water.", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this effect"), icon));
        return levels;
    }

    @Override
    protected void onTick(Player player) {
        player.getWorld().spigot().playEffect(getLocation(player), Effect.WATERDRIP, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
    }
}
