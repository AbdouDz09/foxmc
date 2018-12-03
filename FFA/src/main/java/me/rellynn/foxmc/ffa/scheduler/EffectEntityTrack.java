package me.rellynn.foxmc.ffa.scheduler;

import me.rellynn.foxmc.ffa.FFAPlugin;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
public class EffectEntityTrack extends BukkitRunnable {
    private Entity entity;
    private Effect effect;

    public EffectEntityTrack(Entity entity, Effect effect, int period) {
        this.entity = entity;
        this.effect = effect;
        runTaskTimer(FFAPlugin.get(), 3, period);
    }

    @Override
    public void run() {
        if (entity.isOnGround() || !entity.isValid()) {
            cancel();
            return;
        }
        entity.getWorld().spigot().playEffect(entity.getLocation(), effect, 0, 0, 0, 0, 0, 0, 1, 16);
    }
}
