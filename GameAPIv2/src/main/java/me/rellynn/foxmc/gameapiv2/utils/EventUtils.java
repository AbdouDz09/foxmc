package me.rellynn.foxmc.gameapiv2.utils;

import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by gwennaelguich on 13/06/2017.
 * FoxMC Network.
 */
public class EventUtils {

    public static boolean hasMoved(PlayerMoveEvent evt) {
        return LocationUtils.hasMoved(evt.getFrom(), evt.getTo());
    }

    public static Player getDamager(EntityDamageEvent evt) {
        if (evt instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvt = (EntityDamageByEntityEvent) evt;
            if (damageEvt.getDamager() instanceof Player) {
                return (Player) damageEvt.getDamager();
            } else if (damageEvt.getDamager() instanceof Projectile && ((Projectile) damageEvt.getDamager()).getShooter() instanceof Player) {
                return (Player) ((Projectile) damageEvt.getDamager()).getShooter();
            } else if (damageEvt.getDamager() instanceof TNTPrimed && ((TNTPrimed) damageEvt.getDamager()).getSource() instanceof Player) {
                return (Player) ((TNTPrimed) damageEvt.getDamager()).getSource();
            }
        }
        return null;
    }
}
