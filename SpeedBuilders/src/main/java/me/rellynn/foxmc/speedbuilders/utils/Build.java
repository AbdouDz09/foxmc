package me.rellynn.foxmc.speedbuilders.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gwennaelguich on 01/07/2017.
 * FoxMC Network.
 */
@RequiredArgsConstructor
public class Build {
    @Getter private final String name;
    private final short width;
    private final short height;
    private final short length;
    private final short[] blocks;
    private final byte[] data;
    private final NBTTagCompound[] entities;
    private final Location origin;

    @Getter private ItemStack[] items;
    private int totalSuccess = 0;

    public void load() {
        List<ItemStack> items = new ArrayList<>();
        // Blocks
        int minimumIndex = width * length;
        for (int index = 0; index < blocks.length; index++) {
            if (index >= minimumIndex && blocks[index] != 0) {
                items.addAll(SBUtils.getBuildItems(Material.getMaterial(blocks[index]), data[index]));
                totalSuccess++;
            }
        }
        // Entities
        for (NBTTagCompound nbtTagCompound : entities) {
            // Remove UUID data
            nbtTagCompound.remove("UUIDMost");
            nbtTagCompound.remove("UUIDLeast");
            nbtTagCompound.remove("UUID");
            NBTTagList posList = nbtTagCompound.getList("Pos", 6);
            posList.a(0, new NBTTagDouble(posList.d(0) - origin.getX()));
            posList.a(1, new NBTTagDouble(posList.d(1) - origin.getY()));
            posList.a(2, new NBTTagDouble(posList.d(2) - origin.getZ()));
            nbtTagCompound.set("Pos", posList);
            items.add(SBUtils.getEntitySpawnItem(nbtTagCompound));
            totalSuccess++;
        }
        this.items = items.toArray(new ItemStack[0]);
    }

    public short getBlockId(int x, int y, int z) {
        int index = y * width * length + z * width + x;
        return blocks[index];
    }

    public byte getBlockData(int x, int y, int z) {
        int index = y * width * length + z * width + x;
        return data[index];
    }

    /*
    Platforms
     */
    public void paste(Platform platform) {
        Location paste = platform.getPaste();
        // Blocks
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    Block block = paste.clone().add(x, y, z).getBlock();
                    block.setTypeIdAndData(getBlockId(x, y, z), getBlockData(x, y, z), false);
                }
            }
        }
        // Paste Entities
        for (NBTTagCompound nbtTagCompound : entities) {
            NBTTagList posList = nbtTagCompound.getList("Pos", 6);
            SBUtils.spawnEntity(paste.clone().add(posList.d(0), posList.d(1), posList.d(2)), platform, nbtTagCompound);
        }
    }

    public float getScore(Platform platform) {
        int success = 0;
        // Check blocks
        for (int x = 0; x < width; x++) {
            for (int y = 1; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    short blockId = getBlockId(x, y, z);
                    Block block = platform.getPaste().clone().add(x, y, z).getBlock();
                    if (blockId != 0 && blockId == block.getTypeId() && SBUtils.isSimilar(block.getType(), block.getData(), getBlockData(x, y, z)))
                        success++;
                }
            }
        }
        // Check entities
        for (NBTTagCompound nbtTagCompound : entities) {
            String id = nbtTagCompound.getString("id");
            EntityType type = EntityType.fromName(id);
            NBTTagList posList = nbtTagCompound.getList("Pos", 6);
            Location location = platform.getPaste().clone().add(posList.d(0), posList.d(1), posList.d(2));
            Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 0.1D, 0.1D, 0.1D);
            if (entities.stream().anyMatch(entity -> entity.getType() == type))
                success++;
        }
        return (float) success / totalSuccess;
    }
}
