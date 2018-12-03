package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
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
public class BrownHorseMount extends Mount {

    public BrownHorseMount() {
        super("brown_horse");
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnHorseMount(player, Horse.Variant.HORSE, Horse.Color.BROWN, Horse.Style.BLACK_DOTS);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 900;
        String title = "§6Arabian Horse";
        ItemStack icon = new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData());
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7I'm a versatile breed buddy :3!", "§b§oBrown color", "", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this mount"), icon));
        return levels;
    }
}
