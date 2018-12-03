package me.rellynn.foxmc.speedbuilders.listeners;

import me.rellynn.foxmc.speedbuilders.SBPlugin;
import me.rellynn.foxmc.speedbuilders.game.SBGame;
import me.rellynn.foxmc.speedbuilders.phases.BuildingPhase;
import me.rellynn.foxmc.speedbuilders.phases.JudgeBuildsPhase;
import me.rellynn.foxmc.speedbuilders.phases.ShowBuildPhase;
import me.rellynn.foxmc.speedbuilders.utils.Platform;
import me.rellynn.foxmc.speedbuilders.utils.SBUtils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

/**
 * Created by gwennaelguich on 05/07/2017.
 * FoxMC Network.
 */
public class EntityListener implements Listener {
    private SBGame game = SBPlugin.getGame();

    private void destroyEntity(Entity entity, Player damager) {
        Platform<Player> platform = game.getPlatform(damager);
        if (platform != null && platform == entity.getMetadata("PLATFORM").get(0).value() && platform.isEditAllowed()) {
            NBTTagCompound compound = new NBTTagCompound();
            ((CraftEntity) entity).getHandle().c(compound);
            damager.getInventory().addItem(SBUtils.getEntitySpawnItem(compound));
            damager.updateInventory();
            entity.remove();
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent evt) {
        if (evt.getAttacker() instanceof Player && evt.getVehicle().hasMetadata("PLATFORM") && game.isPhase(BuildingPhase.class))
            destroyEntity(evt.getVehicle(), (Player) evt.getAttacker());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player && evt.getEntity().hasMetadata("PLATFORM") && game.isPhase(BuildingPhase.class))
            destroyEntity(evt.getEntity(), (Player) evt.getDamager());
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent evt) {
        if (evt.getEntity() instanceof FallingBlock) {
            evt.setCancelled(true);
            evt.getEntity().getWorld().playEffect(evt.getEntity().getLocation(), Effect.STEP_SOUND, evt.getBlock().getType());
            evt.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        if (evt.getEntity() instanceof Fireball && game.isPhase(JudgeBuildsPhase.class)) {
            JudgeBuildsPhase phase = (JudgeBuildsPhase) game.getCurrentPhase();
            game.removePlatform(phase.getLooserPlatform());
            if (!game.checkWin()) {
                game.giveRewards();
                game.setPhase(ShowBuildPhase.class);
            }
        }
    }
}
