package me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes;

import me.rellynn.foxmc.api.players.Rank;
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
public class BleedingEffect extends Effect {

    public BleedingEffect() {
        super("bleeding", 30L, Rank.VIP_PLUS);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Bleeding", Arrays.asList("§7Ouchhh. You are bleeding.", "§cRequires " + Rank.VIP_PLUS.getName()), new ItemStack(Material.REDSTONE)));
        return levels;
    }

    @Override
    protected List<Location> buildLocations() {
        return Arrays.asList(new Location(null, 0.0D, 0.0D, 0.0D));
    }

    @Override
    protected void onTick(Player player) {
        player.getWorld().playEffect(getLocation(player), org.bukkit.Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    }
}
