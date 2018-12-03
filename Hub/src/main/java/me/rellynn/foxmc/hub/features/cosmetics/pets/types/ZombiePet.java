package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ColorUtils;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.*;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 23/05/2017.
 * FoxMC Network.
 */
public class ZombiePet extends Pet {
    private static final Random RANDOM = new Random();

    public ZombiePet() {
        super("zombie");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1500;
        String title = "§6Zombie";
        org.bukkit.inventory.ItemStack icon = ItemBuilder.getPlayerSkull("56fc854bb84cf4b7697297973e02b79bc10698460b51a639c60e5e417734e11");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7A Zombie with a flashy armor.", "§7Sometimes it holds an item!", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityZombie zombie = new EntityZombie(((CraftWorld) player.getWorld()).getHandle());
        zombie.setBaby(true);
        int i = RANDOM.nextInt(4);
        if (i == 0) {
            zombie.setEquipment(0, new ItemStack(Items.IRON_SWORD));
        } else if (i == 1) {
            zombie.setEquipment(0, new ItemStack(Items.IRON_SHOVEL));
        }
        zombie.setEquipment(2, CraftItemStack.asNMSCopy(new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(ColorUtils.getRandom()).build()));
        zombie.setEquipment(3, CraftItemStack.asNMSCopy(new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(ColorUtils.getRandom()).build()));
        return Arrays.asList(zombie);
    }
}
