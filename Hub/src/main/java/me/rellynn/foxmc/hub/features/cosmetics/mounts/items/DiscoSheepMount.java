package me.rellynn.foxmc.hub.features.cosmetics.mounts.items;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.Mount;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.DyeColor;
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
public class DiscoSheepMount extends Mount {

    public DiscoSheepMount() {
        super("disco_sheep", Rank.VIP_PLUS);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnSheepMount(player);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Disco Sheep", Arrays.asList("§7Let's have a party today!", "§cRequires " + Rank.VIP_PLUS.getName()), new ItemStack(Material.WOOL, 1, DyeColor.values()[(int) (Math.random() * DyeColor.values().length)].getWoolData())));
        return levels;
    }
}
