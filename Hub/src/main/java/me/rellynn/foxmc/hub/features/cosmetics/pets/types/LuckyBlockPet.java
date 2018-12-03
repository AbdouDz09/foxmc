package me.rellynn.foxmc.hub.features.cosmetics.pets.types;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.hub.features.cosmetics.pets.Pet;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
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
public class LuckyBlockPet extends Pet {

    public LuckyBlockPet() {
        super("lucky_block");
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        int price = 1500;
        String title = "§6Lucky Block";
        ItemStack icon = ItemBuilder.getPlayerSkull("cdd1e6bd215afa5e673285afacb85eb8d0f79a5b46c5432d6feed66097c51248");
        levels.put(0, new LevelInfoBuyable(TAILS, 0, title, Arrays.asList("§7May the luck be with you...", "§6Price: §a" + price + " Tails"), icon));
        levels.put(1, new LevelInfoBuyable(TAILS, price, title, Arrays.asList("§aYou own this pet"), icon));
        return levels;
    }

    @Override
    protected List<Entity> buildEntities(Player player) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        armorStand.lastY = -0.8D;
        armorStand.setSmall(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setEquipment(4, CraftItemStack.asNMSCopy(ItemBuilder.getPlayerSkull("cdd1e6bd215afa5e673285afacb85eb8d0f79a5b46c5432d6feed66097c51248")));
        return Arrays.asList(armorStand);
    }
}
