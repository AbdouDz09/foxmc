package me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes;

import me.rellynn.foxmc.hub.features.cosmetics.effects.Effect;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class EndermanEffect extends Effect {

    public EndermanEffect() {
        super("enderman", 2L);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 700;
        String title = "§6Enderman";
        ItemStack icon = new ItemStack(Material.ENDER_PEARL);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7I'm purple from inside and", "§7black from outside. Err...", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this effect"), icon));
        return levels;
    }

    @Override
    protected List<Location> buildLocations() {
        return Arrays.asList(new Location(null, 0.0D, 0.5D, 0.0D));
    }

    @Override
    protected void onTick(Player player) {
        player.getWorld().spigot().playEffect(getLocation(player), org.bukkit.Effect.PORTAL, 0, 0, 0.3F, 0.5F, 0.3F, 0.0F, 10, 16);
    }
}
