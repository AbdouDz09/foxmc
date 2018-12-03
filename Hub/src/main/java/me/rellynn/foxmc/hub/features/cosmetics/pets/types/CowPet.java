package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCow;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 22/05/2017.
 * FoxMC Network.
 */
public class CowPet extends Pet {

    public CowPet() {
        super("cow");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Cow";
        ItemStack icon = ItemBuilder.getPlayerSkull("c5a9cd58d4c67bccc8fb1f5f756a2d381c9ffac2924b7f4cb71aa9fa13fb5c");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7Moooo!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityCow cow = new EntityCow(((CraftWorld) player.getWorld()).getHandle());
        cow.setAge(-9999);
        return Arrays.asList(cow);
    }
}
