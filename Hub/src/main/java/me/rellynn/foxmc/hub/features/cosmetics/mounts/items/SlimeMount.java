package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.menus.SlimeSizeMenu;
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
public class SlimeMount extends Mount {
    private VirtualMenu sizesMenu = new SlimeSizeMenu();

    public SlimeMount() {
        super("slime");
    }

    @Override
    public void onUse(Player player) {
        sizesMenu.open(player);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 3500;
        String title = "§6Slime Cube";
        ItemStack icon = new ItemStack(Material.SLIME_BALL);
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7I'm a cubic slimy guy!", "§b§oAdjustable size, Bounce", "", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this mount"), icon));
        return levels;
    }
}
