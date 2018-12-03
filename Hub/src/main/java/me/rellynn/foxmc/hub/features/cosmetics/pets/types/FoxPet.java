package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 23/06/2017.
 * FoxMC Network.
 */
public class FoxPet extends Pet {

    public FoxPet() {
        super("fox", Rank.FOX_PLUS);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Fox", Arrays.asList("§7The mascot of the server!", "§cRequires " + Rank.FOX_PLUS.getName()), ItemBuilder.getPlayerSkull("737d8b4494f5a3af7eec87dbb3466cd3dda7835cf5d3a35d1d6a1fa4337e0b3")));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        armorStand.lastY = -0.8D;
        armorStand.setSmall(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setEquipment(4, CraftItemStack.asNMSCopy(ItemBuilder.getPlayerSkull("737d8b4494f5a3af7eec87dbb3466cd3dda7835cf5d3a35d1d6a1fa4337e0b3")));
        return Arrays.asList(armorStand);
    }
}
