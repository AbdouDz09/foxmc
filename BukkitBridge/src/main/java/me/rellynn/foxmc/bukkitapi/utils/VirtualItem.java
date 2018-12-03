package me.rellynn.foxmc.bukkitapi.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public abstract class VirtualItem implements Listener {
    private static final String KEY_ID = "vitem_id";

    @Getter protected String id;
    protected String value;
    private ActionType actionType;
    private int cooldown;
    private Cache<UUID, Long> spamCache;

    public VirtualItem(String id) {
        this(id, ActionType.CUSTOM);
    }

    public VirtualItem(String id, ActionType actionType) {
        this(id, actionType, null, -1);
    }

    public VirtualItem(String id, ActionType actionType, int cooldown) {
        this(id, actionType, null, cooldown);
    }

    public VirtualItem(String id, ActionType actionType, String value) {
        this(id, actionType, value, -1);
    }

    public VirtualItem(String id, ActionType actionType, String value, int cooldown) {
        this.id = id;
        this.actionType = actionType;
        this.value = value;
        this.cooldown = cooldown;
        if (cooldown > 0) {
            this.spamCache = CacheBuilder.newBuilder().weakKeys().expireAfterWrite(cooldown, TimeUnit.SECONDS).build();
        }
        Bukkit.getPluginManager().registerEvents(this, BridgePlugin.get());
    }

    protected boolean isThis(ItemStack item) {
        return item != null && ItemUtils.getString(item, KEY_ID).equals(id);
    }

    protected void resetCooldown(Player player) {
        spamCache.invalidate(player.getUniqueId());
    }

    protected VirtualMenu getUglyMenu(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();
        if (inv.getHolder() instanceof MenuHolder) {
            return ((MenuHolder) inv.getHolder()).getMenu();
        }
        return null;
    }

    protected void uglyUpdate(Player player) {
        VirtualMenu menu = getUglyMenu(player);
        if (menu != null) {
            menu.updateOpened(player);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerWithVirtualItemDeath(PlayerDeathEvent evt) {
        evt.getDrops().removeIf(this::isThis);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVirtualItemDrop(PlayerDropItemEvent evt) {
        if (isThis(evt.getItemDrop().getItemStack())) {
            evt.setCancelled(false);
            evt.getItemDrop().remove();
            evt.getPlayer().getInventory().setItemInHand(evt.getItemDrop().getItemStack());
            performAction(evt.getPlayer(), PerformedAction.DROP);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVirtualItemInteract(PlayerInteractEvent evt) {
        if (evt.getAction() != Action.PHYSICAL && evt.hasItem() && isThis(evt.getItem())) {
            evt.setCancelled(true);
            performAction(evt.getPlayer(), evt.getAction().name().contains("LEFT") ? PerformedAction.LEFT_CLICK : PerformedAction.RIGHT_CLICK);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onVirtualItemClick(InventoryClickEvent evt) {
        if ((evt.getCurrentItem() != null && isThis(evt.getCurrentItem())) || (evt.getCursor() != null && isThis(evt.getCursor()))) {
            evt.setCancelled(true);
            performAction((Player) evt.getWhoClicked(), evt.getClick().isLeftClick() ?
                    PerformedAction.INVENTORY_LEFT : (evt.getClick().isRightClick() ?
                    PerformedAction.INVENTORY_RIGHT : PerformedAction.INVENTORY));
        }
    }

    private void performAction(Player player, PerformedAction action) {
        if (action == PerformedAction.RIGHT_CLICK) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    player.updateInventory();
                }
            }.runTaskLater(BridgePlugin.get(), 1L);
        }
        if (cooldown > 0) {
            Long lastClick = spamCache.getIfPresent(player.getUniqueId());
            if (lastClick != null) {
                int timeLeft = (int) Math.ceil(cooldown - (System.currentTimeMillis() - lastClick) / 1000);
                player.sendMessage("§7Please wait §c" + timeLeft + " second" + (timeLeft > 1 ? "s" : "") + " §7before clicking again!");
                return;
            }
            spamCache.put(player.getUniqueId(), System.currentTimeMillis());
        }
        switch (actionType) {
            case OPEN_MENU:
                VirtualMenu.getMenu(value).open(player);
                break;
            case CLOSE_MENU:
                player.closeInventory();
                break;
            case MESSAGE:
                player.sendMessage(value);
                break;
            case CONNECT:
                FoxAPI.get().getNetworkHandler().sendPlayer(player, value);
                break;
            case COMMAND:
                player.performCommand(value);
                break;
            case BACK:
                VirtualMenu lastMenu = VirtualMenu.removeLastMenu(player);
                if (lastMenu == null) {
                    player.closeInventory();
                    break;
                }
                lastMenu.open(player, false);
                break;
            case CUSTOM:
                onClick(player, action);
                break;
            case NOTHING:
                break;
        }
    }

    protected void onClick(Player player, PerformedAction action) {}

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void setItem(Player player, Inventory inventory, int slot) {
        ItemStack item = getItem(player);
        inventory.setItem(slot, item == null ? null : ItemUtils.setString(item, KEY_ID, id));
    }

    public abstract ItemStack getItem(Player player);

    public enum ActionType {
        OPEN_MENU, CLOSE_MENU, MESSAGE, CONNECT, COMMAND, BACK, CUSTOM, NOTHING
    }

    public enum PerformedAction {
        LEFT_CLICK, RIGHT_CLICK, INVENTORY, INVENTORY_LEFT, INVENTORY_RIGHT, DROP
    }
}
