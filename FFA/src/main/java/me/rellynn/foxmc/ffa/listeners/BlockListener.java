package me.rellynn.foxmc.ffa.listeners;

import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.ffa.FFAGame;
import me.rellynn.foxmc.ffa.FFAPlugin;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 14/06/2017.
 * FoxMC Network.
 */
public class BlockListener implements Listener {
    private final FFAGame game = FFAPlugin.getGame();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent evt) {
        if (!game.canBuild(evt.getPlayer())) {
            Material type = evt.getBlockReplacedState().getType();
            if (evt.getBlock().getType() != Material.TNT) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        evt.getBlock().setType(type);
                    }
                }.runTaskLater(FFAPlugin.get(), 20 * 3);
            } else {
                // TNT special case
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        evt.getBlock().setType(type);
                        Location location = evt.getBlock().getLocation().add(0.5, 0, 0.5);
                        TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
                        Reflection.getField(EntityTNTPrimed.class, "source", EntityLiving.class).set(((CraftTNTPrimed) tnt).getHandle(), ((CraftPlayer) evt.getPlayer()).getHandle());
                        tnt.setFuseTicks(35);
                    }
                }.runTask(FFAPlugin.get());
            }
        }
    }

    /*
    Flint and steel
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent evt) {
        Player player = evt.getPlayer();
        if (player != null && evt.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && !game.canBuild(player)) {
            Location location = evt.getBlock().getLocation();
            location.getWorld().playEffect(location, Effect.PARTICLE_SMOKE, null);
            location.getWorld().playEffect(location, Effect.LAVA_POP, null);
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1, 1);
            int flintSlot = player.getInventory().getHeldItemSlot();
            new BukkitRunnable() {

                @Override
                public void run() {
                    ItemStack flintAndSteel = player.getInventory().getItem(flintSlot);
                    if (flintAndSteel != null) {
                        if (flintAndSteel.getAmount() == 1) {
                            player.getInventory().clear(flintSlot);
                        } else {
                            flintAndSteel.setAmount(flintAndSteel.getAmount() - 1);
                            player.getInventory().setItem(flintSlot, flintAndSteel);
                        }
                        player.updateInventory();
                    }
                }
            }.runTask(FFAPlugin.get());
        }
    }
}
