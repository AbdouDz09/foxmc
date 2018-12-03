package me.rellynn.foxmc.gameapiv2.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 07/06/2017.
 * FoxMC Network.
 */
public class Cuboid {
    protected World world;
    protected int minX, maxX;
    protected int minY, maxY;
    protected int minZ, maxZ;

    public Cuboid(Location loc1, Location loc2) {
        this.world = loc1.getWorld();
        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return maxY - minY;
    }

    public int getLength() {
        return maxZ - minZ;
    }

    public Location getMinLocation() {
        return new Location(world, minX, minY, minZ);
    }

    public Location getMaxLocation() {
        return new Location(world, maxX, maxY, maxZ);
    }

    public Set<Block> getBlocks() {
        Set<Block> blocks = new HashSet<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public void clear() {
        getBlocks().forEach(block -> block.setTypeIdAndData(0, (byte) 0, true));
    }

    public boolean contains(Location location) {
        return world == location.getWorld() && location.getBlockX() >= minX && location.getBlockX() <= maxX && location.getBlockY() >= minY && location.getBlockY() <= maxY && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
    }

    public boolean contains(Block block) {
        return contains(block.getLocation());
    }
}
