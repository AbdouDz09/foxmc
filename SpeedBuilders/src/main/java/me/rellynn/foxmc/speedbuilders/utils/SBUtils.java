package me.rellynn.foxmc.speedbuilders.utils;

import me.rellynn.foxmc.bukkitapi.utils.ItemUtils;
import me.rellynn.foxmc.speedbuilders.SBPlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gwennaelguich on 03/07/2017.
 * FoxMC Network.
 */
public class SBUtils {

    public static String getFormattedTime(int seconds) {
        return (seconds > 3 ? "§a" : (seconds == 3 ? "§e" : (seconds == 2 ? "§6" : "§c"))) + seconds;
    }

    /*
    Blocks
     */
    private static BlockFace getDirection(int data) {
        switch (data) {
            case 0:
                return BlockFace.SOUTH;
            case 1:
                return BlockFace.WEST;
            case 2:
                return BlockFace.NORTH;
            default:
                return BlockFace.EAST;
        }
    }

    private static boolean isAlmostSameDirection(int data, int originalData) {
        BlockFace direction = getDirection(data);
        BlockFace originalDirection = getDirection(originalData);
        return direction == originalDirection || originalDirection == direction.getOppositeFace();
    }

    public static boolean isSimilar(Material type, byte data, byte originalData) {
        switch (type) {
            case LEAVES:
                return TreeSpecies.getByData((byte) (data & 3)) == TreeSpecies.getByData((byte) (originalData & 3));
            case LEAVES_2:
                return TreeSpecies.getByData((byte) ((data & 3) + 4)) == TreeSpecies.getByData((byte) ((originalData & 3) + 4));
            case ANVIL:
                return isAlmostSameDirection(data & 3, originalData & 3);
            case ENDER_PORTAL_FRAME:
                // Check for Eye + Direction
                return (data & 4) == (originalData & 4) && isAlmostSameDirection(data & 3, originalData & 3);
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return isAlmostSameDirection(data, originalData);
            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WOOD_STAIRS:
                // TODO: Corner stairs...
                return data == originalData;
        }
        return data == originalData;
    }

    public static Set<ItemStack> getBuildItems(Material type, int data) {
        Set<ItemStack> items = new HashSet<>();
        if (type != null && type != Material.AIR) {
            switch (type) {
                // Simple blocks
                case BREWING_STAND:
                    items.add(new ItemStack(Material.BREWING_STAND_ITEM));
                    break;
                case CAKE_BLOCK:
                    items.add(new ItemStack(Material.CAKE));
                    break;
                case SKULL:
                    items.add(new ItemStack(Material.SKULL_ITEM, 1, (short) data));
                    break;
                case COCOA:
                    items.add(new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()));
                    break;
                case TRIPWIRE:
                    items.add(new ItemStack(Material.STRING));
                    break;
                case NETHER_WARTS:
                    items.add(new ItemStack(Material.NETHER_STALK));
                    break;
                case CAULDRON:
                    items.add(new ItemStack(Material.CAULDRON_ITEM));
                    break;
                case WOOD_DOUBLE_STEP:
                    items.add(new ItemStack(Material.WOOD_STEP, 2));
                    break;
                case DOUBLE_STEP:
                    items.add(new ItemStack(Material.STEP, 2, (short) data));
                    break;
                case DOUBLE_STONE_SLAB2:
                    items.add(new ItemStack(Material.STONE_SLAB2, 2, (short) data));
                    break;
                case REDSTONE_WIRE:
                    items.add(new ItemStack(Material.REDSTONE));
                    break;
                case REDSTONE_TORCH_OFF:
                    items.add(new ItemStack(Material.REDSTONE_TORCH_ON));
                    break;
                case DIODE_BLOCK_OFF:
                case DIODE_BLOCK_ON:
                    items.add(new ItemStack(Material.DIODE));
                    break;
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                    items.add(new ItemStack(Material.REDSTONE_COMPARATOR));
                    break;
                case REDSTONE_LAMP_OFF:
                case REDSTONE_LAMP_ON:
                    items.add(new ItemStack(Material.REDSTONE_LAMP_OFF));
                    break;
                // Doors
                case ACACIA_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.ACACIA_DOOR_ITEM));
                    }
                    break;
                case BIRCH_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.BIRCH_DOOR_ITEM));
                    }
                    break;
                case DARK_OAK_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.DARK_OAK_DOOR_ITEM));
                    }
                    break;
                case WOODEN_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.WOOD_DOOR));
                    }
                    break;
                case JUNGLE_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.JUNGLE_DOOR_ITEM));
                    }
                    break;
                case SPRUCE_DOOR:
                    if (data != 8) {
                        items.add(new ItemStack(Material.SPRUCE_DOOR_ITEM));
                    }
                    break;
                case IRON_DOOR_BLOCK:
                    if (data != 8) {
                        items.add(new ItemStack(Material.IRON_DOOR));
                    }
                    break;
                // Others
                case BED_BLOCK:
                    if (data != 10) {
                        items.add(new ItemStack(Material.BED));
                    }
                    break;
                case DOUBLE_PLANT:
                    if (data != 10) {
                        items.add(new ItemStack(Material.DOUBLE_PLANT, 1, (short) data));
                    }
                    break;
                case ENDER_PORTAL_FRAME:
                    items.add(new ItemStack(Material.ENDER_PORTAL_FRAME));
                    if (data >= 4) {
                        items.add(new ItemStack(Material.EYE_OF_ENDER));
                    }
                    break;
                case ANVIL:
                    if (data >= 0 && data <= 3) {
                        items.add(new ItemStack(Material.ANVIL));
                    } else if (data >= 4 && data <= 7) {
                        items.add(new ItemStack(Material.ANVIL, 1, (short) 1));
                    } else if (data >= 8 && data <= 11) {
                        items.add(new ItemStack(Material.ANVIL, 1, (short) 2));
                    }
                    break;
                case LEAVES:
                    if (data % 4 == 0) {
                        items.add(new ItemStack(Material.LEAVES));
                    } else if (data % 4 - 1 == 0) {
                        items.add(new ItemStack(Material.LEAVES, 1, (short) 1));
                    } else if (data % 4 - 2 == 0) {
                        items.add(new ItemStack(Material.LEAVES, 1, (short) 2));
                    } else if (data % 4 - 3 == 0) {
                        items.add(new ItemStack(Material.LEAVES, 1, (short) 3));
                    }
                    break;
                case LEAVES_2:
                    if (data % 4 == 0) {
                        items.add(new ItemStack(Material.LEAVES_2));
                    } else if (data % 4 - 1 == 0) {
                        items.add(new ItemStack(Material.LEAVES_2, 1, (short) 1));
                    }
                    break;
                case STEP:
                    if (data % 8 == 0) {
                        items.add(new ItemStack(Material.STEP));
                    } else if (data % 8 - 1 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 1));
                    } else if (data % 8 - 2 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 2));
                    } else if (data % 8 - 3 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 3));
                    } else if (data % 8 - 4 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 4));
                    } else if (data % 8 - 5 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 5));
                    } else if (data % 8 - 6 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 6));
                    } else if (data % 8 - 7 == 0) {
                        items.add(new ItemStack(Material.STEP, 1, (short) 7));
                    }
                    break;
                case STONE_SLAB2:
                    if (data % 8 == 0) {
                        items.add(new ItemStack(Material.STONE_SLAB2));
                    }
                    break;
                case WOOD_STEP:
                    if (data % 8 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP));
                    } else if (data % 8 - 1 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP, 1, (short) 1));
                    } else if (data % 8 - 2 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP, 1, (short) 2));
                    } else if (data % 8 - 3 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP, 1, (short) 3));
                    } else if (data % 8 - 4 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP, 1, (short) 4));
                    } else if (data % 8 - 5 == 0) {
                        items.add(new ItemStack(Material.WOOD_STEP, 1, (short) 5));
                    }
                    break;
                case LOG:
                    if (data % 4 == 0) {
                        items.add(new ItemStack(Material.LOG));
                    } else if (data % 4 - 1 == 0) {
                        items.add(new ItemStack(Material.LOG, 1, (short) 1));
                    } else if (data % 4 - 2 == 0) {
                        items.add(new ItemStack(Material.LOG, 1, (short) 2));
                    } else if (data % 4 - 3 == 0) {
                        items.add(new ItemStack(Material.LOG, 1, (short) 3));
                    }
                    break;
                case LOG_2:
                    if (data % 4 == 0) {
                        items.add(new ItemStack(Material.LOG_2));
                    } else if (data % 4 - 1 == 0) {
                        items.add(new ItemStack(Material.LOG_2, 1, (short) 1));
                    }
                    break;
                default:
                    items.add(new ItemStack(type, 1, (short) data));
                    break;
            }
        }
        return items;
    }

    /*
    Entities
     */
    public static void spawnEntity(Location location, Platform platform, NBTTagCompound nbtTagCompound) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        Entity entity = EntityTypes.a(nbtTagCompound, world);
        entity.setPosition(location.getX(), location.getY(), location.getZ());
        entity.getBukkitEntity().setMetadata("PLATFORM", new FixedMetadataValue(SBPlugin.get(), platform));
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public static void spawnFromItem(Location location, Platform platform, ItemStack item) {
        NBTTagCompound itemCompound = CraftItemStack.asNMSCopy(item).getTag();
        NBTTagCompound nbtTagCompound = itemCompound.getCompound("Entity");
        NBTTagList offsetList = itemCompound.getList("EntityOffset", 6);
        spawnEntity(location.clone().add(offsetList.d(0), offsetList.d(1), offsetList.d(2)), platform, nbtTagCompound);
    }

    public static boolean isSpawningItem(ItemStack item) {
        switch (item.getType()) {
            case MINECART:
            case BOAT:
            case ARMOR_STAND:
            case ITEM_FRAME:
                return true;
        }
        return item.getData() instanceof SpawnEgg;
    }

    public static ItemStack getEntitySpawnItem(NBTTagCompound entity) {
        String id = entity.getString("id");
        EntityType type = EntityType.fromName(id);
        ItemStack item;
        // Item
        switch (type) {
            case BOAT:
                item = new ItemStack(Material.BOAT);
                break;
            case MINECART:
                item = new ItemStack(Material.MINECART);
                break;
            case ARMOR_STAND:
                item = new ItemStack(Material.ARMOR_STAND);
                break;
            case ITEM_FRAME:
                item = new ItemStack(Material.ITEM_FRAME);
                break;
            default:
                item = new SpawnEgg(type).toItemStack(1);
                break;
        }
        // NBT
        NBTTagList posList = entity.getList("Pos", 6);
        NBTTagList offsetList = new NBTTagList();
        offsetList.add(new NBTTagDouble(posList.d(0) - (int) Math.floor(posList.d(0))));
        offsetList.add(new NBTTagDouble(posList.d(1) - (int) Math.floor(posList.d(1))));
        offsetList.add(new NBTTagDouble(posList.d(2) - (int) Math.floor(posList.d(2))));
        NBTTagCompound itemCompound = new NBTTagCompound();
        itemCompound.set("Entity", entity);
        itemCompound.set("EntityOffset", offsetList);
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = ItemUtils.fromItemStack(item);
        nmsCopy.setTag(itemCompound);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }
}
