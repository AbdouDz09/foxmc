package me.rellynn.foxmc.treewars.game.generators;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.utils.Reflection;
import me.rellynn.foxmc.treewars.TWPlugin;
import me.rellynn.foxmc.treewars.game.TWCurrency;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public abstract class Generator {
    @Getter private String id;
    @Getter private Location location;
    @Getter private TWCurrency drop;

    private Map<Integer, GeneratorUpgrade> upgrades;
    private GenUpgradeMenu upgradeMenu;
    private int upgradeLevel;
    private int ticksLeft;
    private Item lastItem;
    private ArmorStand hologram;

    public Generator(String id, Location location, TWCurrency drop, boolean showTime) {
        this.id = id;
        this.location = location;
        this.drop = drop;
        this.upgrades = buildUpgrades();
        this.upgradeMenu = new GenUpgradeMenu(this);
        MetadataValue metaValue = new FixedMetadataValue(TWPlugin.get(), this);
        if (showTime) {
            this.hologram = location.getWorld().spawn(location.clone().add(0.5D, 2.0D, 0.5D), ArmorStand.class);
            this.hologram.setMetadata("GENERATOR", metaValue);
            this.hologram.setGravity(false);
            this.hologram.setVisible(false);
            Item item = location.getWorld().dropItem(location, drop.getItem());
            item.setPickupDelay(32767);
            Reflection.getField(Entity.class, "invulnerable", boolean.class).set(((CraftEntity) item).getHandle(), true);
            this.hologram.setPassenger(item);
            this.hologram.setCustomNameVisible(true);
        }
        location.getBlock().getRelative(BlockFace.DOWN).setMetadata("GENERATOR", metaValue);
        updateTimer();
    }

    protected abstract Map<Integer, GeneratorUpgrade> buildUpgrades();

    public void openUpgrades(Player player) {
        upgradeMenu.open(player);
    }

    public int getLevel() {
        return upgradeLevel + 1;
    }

    public GeneratorUpgrade getUpgrade() {
        return upgrades.get(upgradeLevel);
    }

    public GeneratorUpgrade getNextUpgrade() {
        int nextUpgradeLevel = upgradeLevel + 1;
        if (upgrades.size() == nextUpgradeLevel) {
            return null;
        }
        return upgrades.get(nextUpgradeLevel);
    }

    private void spawn() {
        // Apply the generator upgrade
        ticksLeft = upgrades.get(upgradeLevel).getTicks();
        // Drop item
        Location location = this.location.clone().add(0.5D, 0.0D, 0.5D);
        lastItem = location.getWorld().dropItem(location, drop.getItem());
        lastItem.setMetadata("FROM_GENERATOR", new FixedMetadataValue(TWPlugin.get(), this));
        if (hologram != null) {
            hologram.setCustomName("§eSPAWNED!");
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, null);
        }
    }

    private void updateTimer() {
        if (hologram != null) {
            int seconds = ticksLeft / 20;
            hologram.setCustomName("§eSpawns in §c" + seconds + " §esecond" + (seconds == 1 ? "" : "s"));
        }
    }

    public void upgrade() {
        upgradeLevel++;
    }

    public void tick() {
        if (lastItem != null) {
            lastItem.setVelocity(lastItem.getVelocity().zero()); // Null velocity 1 tick after item spawn
            lastItem = null;
        }
        if (ticksLeft == 0) {
            spawn();
        } else {
            ticksLeft--;
            if (ticksLeft % 20 == 0) {
                updateTimer();
            }
        }
    }
}
