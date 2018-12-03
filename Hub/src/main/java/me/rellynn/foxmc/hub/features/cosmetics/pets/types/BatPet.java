package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityBat;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 23/05/2017.
 * FoxMC Network.
 */
public class BatPet extends Pet {

    public BatPet() {
        super("bat", Rank.VIP_PLUS);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Bat", Arrays.asList("§7My name is... BATMAN!", "§cRequires " + Rank.VIP_PLUS.getName()), ItemBuilder.getPlayerSkull("eeafa2b993239ee75afa7706e8ebddcaed787617af2d5fd3f4b23abe83989")));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityBat bat = new EntityBat(((CraftWorld) player.getWorld()).getHandle());
        bat.setAsleep(false);
        return Arrays.asList(bat);
    }
}
