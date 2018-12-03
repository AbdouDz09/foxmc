package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 23/06/2017.
 * FoxMC Network.
 */
public class SaplingPet extends Pet {

    public SaplingPet() {
        super("sapling");
    }

    @Override
    public boolean canUse(Player player, int level) {
        if (!super.canUse(player, level)) {
            return false;
        } else if (FoxAPI.getPlayer(player).getStatistic("twars.wins") < 100) {
            player.sendMessage("§cYou must have at least 100 wins in TreeWars!");
            return false;
        }
        return true;
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Sapling", Arrays.asList("§7A sapling around your head.", "§cWin §a100 TreeWars games §cto use it!"), new ItemStack(Material.SAPLING)));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        armorStand.lastY = -0.8D;
        armorStand.setSmall(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setEquipment(4, new net.minecraft.server.v1_8_R3.ItemStack(Blocks.SAPLING));
        return Arrays.asList(armorStand);
    }
}
