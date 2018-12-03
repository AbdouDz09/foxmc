package me.rellynn.foxmc.speedbuilders.utils;

import lombok.Getter;
import lombok.Setter;
import me.rellynn.foxmc.gameapiv2.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * Created by gwennaelguich on 02/07/2017.
 * FoxMC Network.
 */
@Getter
public class Platform<T extends Entity> extends Cuboid {
    private T entity;
    private Location paste;
    private Location spawn;
    @Setter private boolean editAllowed = true;

    public Platform(T entity, Location loc1, Location loc2, Location paste, Location spawn) {
        super(loc1, loc2);
        this.entity = entity;
        this.paste = paste;
        this.spawn = spawn;
    }

    public boolean isEntity(T entity) {
        return entity == this.entity;
    }

    public boolean canBuild(Location location) {
        if (!editAllowed || !contains(location)) {
            return false;
        }
        int xDiff = location.getBlockX() - paste.getBlockX();
        int zDiff = location.getBlockZ() - paste.getBlockZ();
        return xDiff >= 0 && xDiff < 7 && zDiff >= 0 && zDiff < 7;
    }

    public void clearBuild() {
        // Remove blocks
        for (int x = paste.getBlockX(); x < paste.getBlockX() + 7; x++) {
            for (int y = paste.getBlockY() + 1; y <= maxY; y++) {
                for (int z = paste.getBlockZ(); z < paste.getBlockZ() + 7; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR, false);
                }
            }
        }
        // Remove entities
        paste.getWorld().getEntities().forEach(entity -> {
            if (entity.hasMetadata("PLATFORM") && entity.getMetadata("PLATFORM").get(0).value() == this)
                entity.remove();
        });
    }

    @Override
    public void clear() {
        clearBuild();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR, false);
                }
            }
        }
    }
}
