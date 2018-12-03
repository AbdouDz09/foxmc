package me.rellynn.foxmc.hub.features.cosmetics.gadgets.types;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.LocationUtils;
import me.rellynn.foxmc.bukkitapi.utils.PacketUtils;
import me.rellynn.foxmc.hub.HubPlugin;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.Gadget;
import me.rellynn.foxmc.hub.shops.LevelInfoBuyable;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

import static me.rellynn.foxmc.hub.shops.Currency.TAILS;

/**
 * Created by gwennaelguich on 20/05/2017.
 * FoxMC Network.
 */
public class RainbowTrailGadget extends Gadget {
    private Map<UUID, Set<Location>> blocks = new HashMap<>();

    public RainbowTrailGadget() {
        super("rainbow_trail", Rank.VIP_PLUS, 20, 15);
    }

    @Override
    protected Map<Integer, LevelInfoBuyable> buildBuyableLevels() {
        Map<Integer, LevelInfoBuyable> levels = new HashMap<>();
        levels.put(0, new LevelInfoBuyable(TAILS, 0, "§6Rainbow Trail", Arrays.asList("§7A trail that follows you.", "§cRequires " + Rank.VIP_PLUS.getName()), new ItemStack(Material.LEATHER_BOOTS)));
        return levels;
    }

    private void broadcastBlockChange(Location location, Material material, byte data, boolean forceSend) {
        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld) location.getWorld()).getHandle(), position);
        packet.block = net.minecraft.server.v1_8_R3.Block.getByCombinedId(material.getId() + (data << 12));
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> {
            if (forceSend || Settings.hubVisibility.is(FoxAPI.getPlayer(player), SettingValue.ENABLED)) {
                PacketUtils.sendPacket(player, packet);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getClickedInventory() instanceof PlayerInventory && evt.getCurrentItem() != null && evt.getCurrentItem().getType() == Material.LEATHER_BOOTS && blocks.containsKey(evt.getWhoClicked().getUniqueId()))
            evt.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        if (!blocks.containsKey(player.getUniqueId())) {
            return;
        }
        DyeColor dyeColor = DyeColor.values()[(int) (Math.random() * DyeColor.values().length)];
        Color color = dyeColor.getColor();
        player.getWorld().spigot().playEffect(player.getLocation().add(0.0D, 0.1D, 0.0D), Effect.COLOURED_DUST, 0, 1, color.getRed(), color.getGreen(), color.getBlue(), 1.0F, 0, 16);
        if (LocationUtils.hasMoved(evt.getFrom(), evt.getTo())) {
            Block block = evt.getTo().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType().isSolid()) {
                Location location = block.getLocation();
                broadcastBlockChange(location, Material.STAINED_CLAY, dyeColor.getWoolData(), false);
                player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(color).build());
                Set<Location> locations = blocks.get(player.getUniqueId());
                if (locations.add(location)) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Block block = location.getBlock();
                            broadcastBlockChange(location, block.getType(), block.getData(), true);
                            locations.remove(location);
                        }
                    }.runTaskLater(HubPlugin.get(), 30);
                }
            }
        }
    }

    @Override
    public void onStart(Player player) {
        blocks.put(player.getUniqueId(), new HashSet<>());
    }

    @Override
    public void onEnd(Player player) {
        blocks.remove(player.getUniqueId()).forEach(location -> {
            Block block = location.getBlock();
            broadcastBlockChange(location, block.getType(), block.getData(), true);
        });
        player.getInventory().setBoots(null);
    }
}
