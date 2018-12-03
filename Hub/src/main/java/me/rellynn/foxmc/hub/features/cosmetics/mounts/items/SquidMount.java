package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.api.players.Rank;
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
public class SquidMount extends Mount {

    public SquidMount() {
        super("squid", Rank.FOX_PLUS);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnSquidMount(player);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Squid", Arrays.asList("§7Did you know that squids are good birds?", "§b§oCan Fly", "", "§cRequires " + Rank.FOX_PLUS.getName()), new ItemStack(Material.INK_SACK)));
        return levels;
    }
}
