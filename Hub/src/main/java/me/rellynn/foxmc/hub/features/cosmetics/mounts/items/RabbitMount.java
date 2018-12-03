package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public class RabbitMount extends Mount {

    public RabbitMount() {
        super("rabbit");
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnRabbitMount(player);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1500;
        String title = "§6Bunny";
        ItemStack icon = new ItemStack(Material.RABBIT_HIDE);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Ride your cutie bunny and", "§7Enjoy your ride!", "§b§oLeap", "", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this mount"), icon));
        return levels;
    }
}
