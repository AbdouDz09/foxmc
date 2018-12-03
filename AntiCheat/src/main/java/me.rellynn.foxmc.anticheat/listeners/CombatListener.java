package me.rellynn.foxmc.anticheat.listeners;

import me.rellynn.foxmc.anticheat.checks.combat.AimbotCheck;
import me.rellynn.foxmc.anticheat.checks.combat.AngleCheck;
import me.rellynn.foxmc.anticheat.checks.combat.CriticalsCheck;
import me.rellynn.foxmc.anticheat.checks.combat.FastBowCheck;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import me.rellynn.foxmc.anticheat.tasks.DelayNoSwingCheck;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;

import java.lang.ref.WeakReference;

/**
 * Created by gwennaelguich on 21/07/2017.
 * FoxMC Network.
 */
public class CombatListener implements Listener {
    private static final FastBowCheck BOW_CHECK = new FastBowCheck();
    private static final CriticalsCheck CRITICALS_CHECK = new CriticalsCheck();
    private static final AngleCheck ANGLE_CHECK = new AngleCheck();
    private static final AimbotCheck AIMBOT_CHECK = new AimbotCheck();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerShootArrow(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player && !BOW_CHECK.passCheck((Player) evt.getEntity(), ACPlayer.get((Player) evt.getEntity()), evt.getForce())) {
            evt.setCancelled(true);
        } else {
            // Tricky way to make BowAimbot useless
            evt.getProjectile().setVelocity(evt.getProjectile().getVelocity().multiply(1.1D));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent evt) {
        if (evt.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            ACPlayer acPlayer = ACPlayer.get(evt.getPlayer());
            acPlayer.fightData.lastSwingTime = System.currentTimeMillis();
            acPlayer.lastItemInHand = evt.getPlayer().getItemInHand();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof FishHook) {
            FishHook hook = (FishHook) evt.getDamager();
            if (hook.getShooter() instanceof Player) {
                ACPlayer.get((Player) hook.getShooter()).fightData.lastRodEntity = new WeakReference<>(evt.getEntity());
            }
        } else if (evt.getDamager() instanceof Player) {
            Player damager = (Player) evt.getDamager();
            ACPlayer acPlayer = ACPlayer.get(damager);
            acPlayer.fightData.lastRodEntity = null;
            Entity attacked = evt.getEntity();
            ANGLE_CHECK.check(damager, acPlayer, acPlayer.movingData, attacked);
            new DelayNoSwingCheck(damager, acPlayer); // Delay NoSwing check (fix lag issues)
            if (attacked instanceof LivingEntity && evt.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                AIMBOT_CHECK.check(damager, acPlayer, acPlayer.movingData, (LivingEntity) attacked);
                double originalDamage = evt.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
                if (!CRITICALS_CHECK.passCheck(damager, acPlayer, acPlayer.movingData, originalDamage)) {
                    originalDamage /= 1.5D;
                    evt.setDamage(EntityDamageEvent.DamageModifier.BASE, originalDamage);
                }
            }
        }
    }
}
