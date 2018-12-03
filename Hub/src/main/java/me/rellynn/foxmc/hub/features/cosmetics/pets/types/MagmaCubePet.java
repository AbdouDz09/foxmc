package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
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
public class MagmaCubePet extends Pet {

    public MagmaCubePet() {
        super("magma_cube");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1200;
        String title = "§6Magma Cube";
        ItemStack icon = ItemBuilder.getPlayerSkull("38957d5023c937c4c41aa2412d43410bda23cf79a9f6ab36b76fef2d7c429");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7A cube straight from hell.", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityMagmaCube magmaCube = new EntityMagmaCube(((CraftWorld) player.getWorld()).getHandle());
        magmaCube.setSize(1);
        return Arrays.asList(magmaCube);
    }
}
