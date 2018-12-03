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
public class UndeadHorseMount extends Mount {

    public UndeadHorseMount() {
        super("undead_horse", Rank.FOX);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        MountsManager.spawnHorseMount(player, Horse.Variant.UNDEAD_HORSE, Horse.Color.WHITE, Horse.Style.NONE);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Undead Horse", Arrays.asList("§7I'm still alive and health.", "§7Don't worry bud!", "§cRequires " + Rank.FOX.getName()), new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal())));
        return levels;
    }
}
