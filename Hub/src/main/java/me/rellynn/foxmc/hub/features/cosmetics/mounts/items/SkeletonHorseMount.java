package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Material;
import org.bukkit.SkullType;
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
public class SkeletonHorseMount extends Mount {

    public SkeletonHorseMount() {
        super("skeleton_horse", Rank.FOX_PLUS);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnHorseMount(player, Horse.Variant.SKELETON_HORSE, Horse.Color.WHITE, Horse.Style.NONE);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Dead Horse", Arrays.asList("§7Wants to try some of my", "§7horrible rides?!", "§cRequires " + Rank.FOX_PLUS.getName()), new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.SKELETON.ordinal())));
        return levels;
    }
}
