package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
public class AdventurerPet extends Pet {

    public AdventurerPet() {
        super("adventurer", Rank.FOX);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Adventurer", Arrays.asList("§7Make a little clone of you!", "§cRequires " + Rank.FOX.getName()), new org.bukkit.inventory.ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        armorStand.setArms(true);
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(true);
        // Custom equipment
        armorStand.setEquipment(4, CraftItemStack.asNMSCopy(new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()).setSkullProfile(((CraftPlayer) player).getProfile()).build()));
        armorStand.setEquipment(3, new ItemStack(Items.LEATHER_CHESTPLATE));
        armorStand.setEquipment(2, new ItemStack(Items.LEATHER_LEGGINGS));
        armorStand.setEquipment(1, new ItemStack(Items.LEATHER_BOOTS));
        return Arrays.asList(armorStand);
    }
}
