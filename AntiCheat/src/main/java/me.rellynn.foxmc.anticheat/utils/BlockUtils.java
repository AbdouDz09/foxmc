package me.rellynn.foxmc.anticheat.utils;


import org.bukkit.block.Block;

/**
 * Created by gwennaelguich on 18/07/2017.
 * FoxMC Network.
 */
public class BlockUtils {

    public static float getFrictionFactor(Block block) {
        switch (block.getType()) {
            case AIR:
                return 0.91F;
            case SLIME_BLOCK:
                return 0.6F;
            case ICE:
            case PACKED_ICE:
                return 0.98F;
            default:
                return 0.6F;
        }
    }

    public static boolean isChest(Block block) {
        switch (block.getType()) {
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSlab(Block block) {
        switch (block.getType()) {
            case STEP:
            case WOOD_STEP:
            case STONE_SLAB2:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStairs(Block block) {
        switch (block.getType()) {
            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WOOD_STAIRS:
                return true;
            default:
                return false;
        }
    }

    public static boolean isFence(Block block) {
        switch (block.getType()) {
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInteractable(Block block) {
        switch (block.getType()) {
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case FENCE:
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case TRAP_DOOR:
            case IRON_TRAPDOOR:
            case DAYLIGHT_DETECTOR:
            case DAYLIGHT_DETECTOR_INVERTED:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case FURNACE:
            case BURNING_FURNACE:
            case WORKBENCH:
            case DISPENSER:
            case DROPPER:
            case CAULDRON:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case CHEST:
            case TRAPPED_CHEST:
            case ENDER_CHEST:
            case ANVIL:
            case BEACON:
            case ENCHANTMENT_TABLE:
            case BREWING_STAND:
            case COMMAND:
            case HOPPER:
            case LEVER:
            case STONE_BUTTON:
            case WOOD_BUTTON:
            case SIGN_POST:
            case WALL_SIGN:
            case DRAGON_EGG:
                return true;
            default:
                return false;
        }
    }
}
