package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class AntiGravityGadget extends Gadget {
    private static final Reflection.FieldAccessor<Integer> FLY_TICKS = Reflection.getField(PlayerConnection.class, "g", int.class);

    private Map<UUID, ArmorStand> armorStands = new HashMap<>();

    public AntiGravityGadget() {
        super("anti_gravity", Rank.FOX, 40, 10);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Anti Gravity", Arrays.asList("§7Do you want to try the 0G?", "§7This gadget creates a non", "§7gravitational field!", "§cRequires " + Rank.FOX.getName()), new ItemStack(Material.ARMOR_STAND)));
        return levels;
    }

    @Override
    public void onStart(Player player) {
        ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
        armorStands.put(player.getUniqueId(), armorStand);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.SEA_LANTERN));
    }

    @Override
    public void onTick(Player player, int ticksLeft) {
        ArmorStand armorStand = armorStands.get(player.getUniqueId());
        armorStand.setHeadPose(armorStand.getHeadPose().add(0.0D, 1.0D, 0.0D));
        armorStand.getWorld().spigot().playEffect(armorStand.getLocation(), Effect.PORTAL, 0, 0, 3.0F, 3.0F, 3.0F, 0.0F, 150, 16);
        armorStand.getWorld().spigot().playEffect(armorStand.getEyeLocation(), Effect.WITCH_MAGIC, 0, 0, 0.3F, 0.3F, 0.3F, 0.0F, 5, 16);
        for (Entity entity : armorStand.getNearbyEntities(3.0D, 2.0D, 3.0D)) {
            if (entity instanceof LivingEntity
                    && !(entity instanceof ArmorStand)
                    && (!(entity instanceof Player) || Settings.hubVisibility.is(FoxAPI.getPlayer((Player) entity), SettingValue.ENABLED))) {
                entity.setVelocity(entity.getVelocity().setY(0.05F));
                if (entity instanceof Player) {
                    FLY_TICKS.set(((CraftPlayer) player).getHandle().playerConnection, 0);
                }
            }
        }
    }

    @Override
    public void onEnd(Player player) {
        armorStands.remove(player.getUniqueId()).remove();
    }
}
