package me.rellynn.foxmc.anticheat.data;

import com.google.common.collect.EvictingQueue;
import me.rellynn.foxmc.anticheat.ACConfig;
import me.rellynn.foxmc.anticheat.utils.BlockUtils;
import me.rellynn.foxmc.anticheat.utils.PlayerUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by gwennaelguich on 17/07/2017.
 * FoxMC Network.
 */
public class MovingData {
    /*
    Packets
     */
    public int packetsCount;
    public Queue<PacketPlayInFlying> previousPackets = EvictingQueue.create(5);
    /*
    Locations
     */
    public Location currentLocation;
    public Location previousLocation;
    public Location nextLocation;
    /*
    Velocities
     */
    public double clientVelX;
    public double clientVelZ;
    public Vector serverVelocity;
    /*
    Ground-related values
     */
    public Location lastGroundLocation;
    public boolean wasOnGround;
    public boolean onGround;
    public boolean clientOnGround;
    public float fallDistance;
    public double previousYDiff;
    public double highestY;
    /*
    Blocks collisions values
     */
    public boolean wasOnLadder;
    public boolean wasInLiquid;
    public boolean isOnLadder;
    public boolean isInWater;
    public boolean isInLava;
    public boolean isInLiquid;
    public boolean isInCobweb;
    public boolean hasCeiling;
    /*
    Bypass
     */
    public long lastSneak;
    public long lastSprint;
    public long lastWalk;
    public long lastJump;
    public long lastOnIce;
    public long lastHasCeiling;
    public long lastStairsOrSlab;
    public long lastLiquidSurface;
    public boolean justTeleported;
    /*
    Speed check values
     */
    public long lastSpeedEffect;
    public int lastSpeedLvl;
    private long inLiquidTicks;
    private long inCobwebTicks;
    private long onSoulsandTicks;
    /*
    Private data
     */
    private Player player;
    private ACPlayer acPlayer;

    MovingData(Player player, ACPlayer acPlayer) {
        this.player = player;
        this.acPlayer = acPlayer;
    }

    public boolean isAscending() {
        return serverVelocity.getY() > 0.0D;
    }

    public boolean isDescending() {
        return serverVelocity.getY() < 0.0D;
    }

    public boolean isFalling() {
        return !onGround && fallDistance > 0.0D;
    }

    public boolean hasVelocity() {
        return clientVelX != 0.0D || clientVelZ != 0.0D;
    }

    public void addVelocity(double x, double z) {
        clientVelX += x;
        clientVelZ += z;
    }

    public void updateClientVelocity() {
        float friction = 0.91F;
        if (onGround) {
            friction *= BlockUtils.getFrictionFactor(currentLocation.getBlock().getRelative(BlockFace.DOWN));
        }
        clientVelX *= friction;
        if (Math.abs(clientVelX) < 0.005D) {
            clientVelX = 0.0D;
        }
        clientVelZ *= friction;
        if (Math.abs(clientVelZ) < 0.005D) {
            clientVelZ = 0.0D;
        }
    }

    public int getMaxMovePacketsPerTick() {
        int ping = acPlayer.getPing(); // Using highest ping to determine max packets/second
        return (int) Math.ceil((ACConfig.MAX_MOVE_PACKETS + 10 * (ping / (double) 100)) / 20);
    }

    public float getMaxVerticalDistance() {
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            int jumpLevel = PlayerUtils.getPotionLevel(player, PotionEffectType.JUMP);
            if (jumpLevel == 1) {
                return 1.823F;
            } else if (jumpLevel == 2) {
                return 2.496F;
            } else if (jumpLevel == 3) {
                return 3.263F;
            } else if (jumpLevel == 4) {
                return 3.815F;
            } else if (jumpLevel == 5) {
                return 4.652F;
            } else if (jumpLevel > 0) {
                return Float.MAX_VALUE;
            }
        }
        return ACConfig.BASE_JUMP_HEIGHT;
    }

    public float getMaxHorizontalDistance() {
        long now = System.currentTimeMillis();
        float speed = player.getWalkSpeed() / 0.2F;
        boolean inLiquid = inLiquidTicks >= 10L,
                inCobweb = inCobwebTicks >= 5L,
                onSoulsand = onSoulsandTicks >= 10L,
                onIce = now - lastOnIce < 800L,
                hasCeiling = now - lastHasCeiling < 1000L,
                onStairsOrSlab = now - lastStairsOrSlab < 1000L;
        boolean isJumping = now - lastJump < 1000L,
                isSprinting = now - lastSprint < 1000L,
                isWalking = now - lastWalk < 1000L,
                isSneaking = now - lastSneak < 1000L;
        if (lastSpeedLvl > 0 && now - lastSpeedEffect < 1000L) {
            speed *= lastSpeedLvl < 3 ? (1 + 0.2F * lastSpeedLvl) : (1 + 0.4F * lastSpeedLvl);
        } else if (player.hasPotionEffect(PotionEffectType.SLOW)) {
            speed *= 1 - (0.15F * PlayerUtils.getPotionLevel(player, PotionEffectType.SLOW));
        }
        if (inLiquid) {
            boolean onSurface = now - lastLiquidSurface < 500L;
            speed *= (onSurface) ? 0.022F : 0.015F;
        } else if (inCobweb) {
            speed *= 0.01F;
        } else if (onSoulsand) {
            speed *= 0.031F;
        } else if (onIce) {
            speed *= 1.03F;
        } else if (isSprinting || isWalking) {
            speed *= (hasCeiling && isJumping) ? 0.41F : ((isJumping || onStairsOrSlab) ? 0.375F : 0.152F);
        } else if (isSneaking) {
            speed *= isJumping ? 0.015F : 0.016F;
        }
        return speed;
    }

    /*
    Ugly NMS methods
     */
    private List<AxisAlignedBB> getCollisionsBoxes(AxisAlignedBB playerBox, Map<BlockPosition, Material> blocks) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        List<AxisAlignedBB> boxes = new ArrayList<>();
        blocks.forEach((blockPosition, material) -> {
            IBlockData data = entityPlayer.world.getType(blockPosition);
            data.getBlock().a(entityPlayer.world, blockPosition, data, playerBox, boxes, entityPlayer);
        });
        return boxes;
    }

    private Map<BlockPosition, Material> getCollidingBlocks(AxisAlignedBB playerBox) {
        // Took from NMS
        World world = ((CraftWorld) player.getWorld()).getHandle();
        BlockPosition pos1 = new BlockPosition(playerBox.a, playerBox.b, playerBox.c),
                pos2 = new BlockPosition(playerBox.d, playerBox.e, playerBox.f);
        Map<BlockPosition, Material> blocks = new HashMap<>();
        for (int x = pos1.getX(); x <= pos2.getX(); x++) {
            for (int y = pos1.getY(); y <= pos2.getY(); y++) {
                for (int z = pos1.getZ(); z <= pos2.getZ(); z++) {
                    BlockPosition position = new BlockPosition(x, y, z);
                    Block block = world.getType(position, false).getBlock();
                    if (block != Blocks.AIR) {
                        blocks.put(position, Material.getMaterial(Block.getId(block)));
                    }
                }
            }
        }
        return blocks;
    }

    /*
    Update moving data
     */
    public void updateMovingData(Location to, boolean teleport) {
        // Ugly NMS part
        Location location = player.getLocation();
        double xOffset = to.getX() - location.getX(),
                yOffset = to.getY() - location.getY(),
                zOffset = to.getZ() - location.getZ();
        AxisAlignedBB playerBox = ((CraftPlayer) player).getHandle().getBoundingBox().c(xOffset, yOffset, zOffset),
                groundPlayerBox = playerBox.c(0.0D, -0.1D, 0.0D),
                ceilingPlayerBox = playerBox.c(0.0D, 0.25D, 0.0D).grow(0.1D, 0.0D, 0.1D);
        // Update moving data
        if (teleport) {
            justTeleported = true;
            previousYDiff = 0.0D;
            highestY = to.getY();
        } else {
            // Update previous y-diff for Gravity check
            if (previousLocation != null) {
                double yDiff = currentLocation.getY() - previousLocation.getY();
                if (Math.abs(yDiff) < 0.05D) {
                    yDiff = 0.0D;
                }
                previousYDiff = yDiff;
            }
        }
        wasOnGround = onGround;
        double relativeY = to.getY() % 1;
        onGround = (relativeY == 0.0D && to.getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
                || (relativeY == 0.015625D && to.getBlock().getType() == Material.WATER_LILY)
                || (relativeY == 0.0625D && to.getBlock().getType() == Material.CARPET)
                || (relativeY == 0.1875D && (to.getBlock().getType() == Material.TRAP_DOOR || to.getBlock().getType() == Material.IRON_TRAPDOOR))
                || (relativeY == 0.3125D && to.getBlock().getType() == Material.CAULDRON)
                || (relativeY == 0.5D && (BlockUtils.isSlab(to.getBlock()) || BlockUtils.isStairs(to.getBlock()) || BlockUtils.isFence(to.getBlock().getRelative(BlockFace.DOWN))))
                || (relativeY == 0.75D && to.getBlock().getType() == Material.ENCHANTMENT_TABLE)
                || (relativeY == 0.875D && (to.getBlock().getType() == Material.SOUL_SAND || BlockUtils.isChest(to.getBlock())))
                || (relativeY == 0.9375D && to.getBlock().getType() == Material.CACTUS)
                || (relativeY % 0.125D == 0 && to.getBlock().getType() == Material.SNOW)
                || !getCollisionsBoxes(groundPlayerBox, getCollidingBlocks(groundPlayerBox.grow(0.0D, 1.0D, 0.0D))).isEmpty();
        if (onGround) {
            lastGroundLocation = to;
        }
        serverVelocity = player.getVelocity();
        previousLocation = currentLocation;
        currentLocation = to;
        // Check blocks collisions
        wasInLiquid = isInLiquid;
        wasOnLadder = isOnLadder;
        // New states
        Map<BlockPosition, Material> aroundBlocks = getCollidingBlocks(playerBox);
        isInCobweb = aroundBlocks.values().contains(Material.WEB);
        isInWater = aroundBlocks.values().contains(Material.WATER) || aroundBlocks.values().contains(Material.STATIONARY_WATER);
        isInLava = aroundBlocks.values().contains(Material.LAVA) || aroundBlocks.values().contains(Material.STATIONARY_LAVA);
        isInLiquid = isInWater || isInLava;
        isOnLadder = to.getBlock().getType() == Material.LADDER || to.getBlock().getType() == Material.VINE;
        hasCeiling = !getCollisionsBoxes(ceilingPlayerBox, getCollidingBlocks(ceilingPlayerBox)).isEmpty();
        // Update ticks
        if (isInCobweb) {
            inCobwebTicks++;
        } else {
            inCobwebTicks = 0L;
        }
        if (onGround && to.getBlock().getType() == Material.SOUL_SAND) {
            onSoulsandTicks++;
        } else {
            onSoulsandTicks = 0L;
        }
        if (isInLiquid) {
            inLiquidTicks++;
        } else {
            inLiquidTicks = 0L;
        }
    }
}
