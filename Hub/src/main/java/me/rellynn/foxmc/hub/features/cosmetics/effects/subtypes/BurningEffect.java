package me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.hub.features.cosmetics.effects.Effect;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
public class BurningEffect extends Effect {

    public BurningEffect() {
        super("burning", 3L, Rank.VIP);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Burning", Arrays.asList("§7Ignites you.", "§cRequires " + Rank.VIP.getName()), new ItemStack(Material.FLINT_AND_STEEL)));
        return levels;
    }

    @Override
    protected List<Location> buildLocations() {
        return Arrays.asList(new Location(null, 0.0D, 1.0D, 0.0D));
    }

    @Override
    protected void onTick(Player player) {
        player.setFireTicks(Integer.MAX_VALUE);
        Location location = getLocation(player);
        World world = location.getWorld();
        world.spigot().playEffect(location, org.bukkit.Effect.LAVA_POP, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
        world.spigot().playEffect(location, org.bukkit.Effect.PARTICLE_SMOKE, 0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 1, 16);
    }

    @Override
    protected void onDisable(Player player) {
        player.setFireTicks(0);
    }
}
