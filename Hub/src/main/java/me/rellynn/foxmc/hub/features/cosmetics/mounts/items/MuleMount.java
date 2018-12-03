package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.COINS;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public class MuleMount extends Mount {

    public MuleMount() {
        super("mule");
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnHorseMount(player, Horse.Variant.MULE, Horse.Color.WHITE, Horse.Style.NONE);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Mule";
        ItemStack icon = new ItemStack(Material.WHEAT);
        levels.put(0, new LevelInfoBuyable(COINS, 0, title, Arrays.asList("§7Am I a male or a female?", "§6Price: §e" + price + " FC"), icon));
        levels.put(1, new LevelInfoBuyable(COINS, price, title, Arrays.asList("§aYou own this mount"), icon));
        return levels;
    }
}
