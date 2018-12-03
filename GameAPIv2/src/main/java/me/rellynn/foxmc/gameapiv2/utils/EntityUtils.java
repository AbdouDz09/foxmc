package me.rellynn.foxmc.gameapiv2.utils;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Created by gwennaelguich on 04/07/2017.
 * FoxMC Network.
 */
public class EntityUtils {

    public static void setPosition(Entity entity, Location location) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        nmsEntity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        nmsEntity.f(nmsEntity.yaw);
        nmsEntity.g(nmsEntity.yaw);
    }

    public static void lookAt(Entity entity, Location target) {
        Location location = entity.getLocation();
        double dx = target.getX() - location.getX();
        double dy = target.getY() - location.getY();
        double dz = target.getZ() - location.getZ();
        if (dx != 0) {
            if (dx < 0) {
                location.setYaw((float) (1.5 * Math.PI));
            } else {
                location.setYaw((float) (0.5 * Math.PI));
            }
            location.setYaw(location.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            location.setYaw((float) Math.PI);
        }
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
        location.setPitch((float) -Math.atan(dy / dxz));
        location.setYaw(-location.getYaw() * 180.0F / (float) Math.PI);
        location.setPitch(location.getPitch() * 180.0F / (float) Math.PI);
        setPosition(entity, location);
    }

}
