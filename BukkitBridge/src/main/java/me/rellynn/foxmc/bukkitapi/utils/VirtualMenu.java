package me.rellynn.foxmc.bukkitapi.utils;

import lombok.Getter;
import me.rellynn.foxmc.bukkitapi.BridgePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class VirtualMenu implements Listener {
    private static final Map<String, VirtualMenu> KNOWN_MENUS = new HashMap<>();
    private static final Map<UUID, Stack<String>> OPEN_HISTORY = new HashMap<>();

    public static VirtualMenu getMenu(String id) {
        return KNOWN_MENUS.get(id);
    }

    public static VirtualMenu removeLastMenu(Player player) {
        Stack<String> stack = OPEN_HISTORY.get(player.getUniqueId());
        if (stack != null && !stack.isEmpty()) {
            return getMenu(stack.pop());
        }
        return null;
    }

    @Getter protected String id;
    protected int size;
    protected String title;
    private Map<Integer, VirtualItem> display = new HashMap<>();
    private Map<Player, Inventory> opened = new WeakHashMap<>();
    private MenuHolder holder = new MenuHolder(this);

    public VirtualMenu(String id, String title) {
        this(id, title, -1L);
    }

    public VirtualMenu(String id, String title, long updateTicks) {
        this(id, 1, title, updateTicks);
    }

    public VirtualMenu(String id, int size, String title) {
        this(id, size, title, -1L);
    }

    public VirtualMenu(String id, int size, String title, long updateTicks) {
        this.id = id;
        this.size = size;
        this.title = title;
        if (updateTicks > 0) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    updateAllOpened();
                }
            }.runTaskTimer(BridgePlugin.get(), 0L, updateTicks);
        }
        Bukkit.getPluginManager().registerEvents(this, BridgePlugin.get());
        KNOWN_MENUS.put(id, this);
    }

    private boolean isThis(Inventory inv) {
        return inv != null && inv.getHolder() == holder;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        if (isThis(evt.getPlayer().getOpenInventory().getTopInventory())) {
            opened.remove(evt.getPlayer());
            OPEN_HISTORY.remove(evt.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (isThis(evt.getInventory())) {
            Player player = (Player) evt.getPlayer();
            opened.remove(player);
            onClose(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent evt) {
        if (isThis(evt.getClickedInventory()) || (evt.isShiftClick() && isThis(evt.getInventory()))) {
            evt.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent evt) {
        if (isThis(evt.getInventory())) {
            evt.setCancelled(true);
        }
    }

    protected void onUpdate() {}

    protected void onOpen(Player player) {}

    protected void onUpdate(Player player) {}

    protected void onClose(Player player) {}

    protected void clearDisplay() {
        display.clear();
    }

    protected void removeItem(int slot) {
        display.remove(slot);
    }

    private void setItems(Player player, Inventory inventory) {
        inventory.clear();
        display.forEach((slot, value) -> value.setItem(player, inventory, slot));
    }

    protected int getSlot(int column, int line) {
        return (line - 1) * 9 + (column - 1);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void addItemToDisplay(int slot, VirtualItem item) {
        if (slot >= size * 9) {
            // Resize
            size = (int) (Math.floor(slot / (double) 9)) + 1;
        }
        display.put(slot, item);
    }

    public void updateAllOpened() {
        onUpdate();
        opened.keySet().forEach(this::updateOpened);
    }

    public void updateOpened(Player player) {
        Inventory inventory = opened.get(player);
        if (inventory != null) {
            onUpdate(player);
            if (inventory.getSize() != size * 9) {
                open(player, false);
            } else {
                setItems(player, inventory);
            }
        }
    }

    public void open(Player player, boolean saveHistory) {
        Inventory inventory = Bukkit.createInventory(holder, size * 9, title);
        setItems(player, inventory);
        opened.put(player, inventory);
        if (saveHistory) {
            Inventory lastInventory = player.getOpenInventory().getTopInventory();
            if (lastInventory == null || !(lastInventory.getHolder() instanceof MenuHolder)) {
                OPEN_HISTORY.remove(player.getUniqueId());
            } else if (lastInventory.getHolder() != holder) {
                VirtualMenu lastMenu = ((MenuHolder) lastInventory.getHolder()).getMenu();
                OPEN_HISTORY.computeIfAbsent(player.getUniqueId(), uuid -> new Stack<>()).add(lastMenu.getId());
            }
        }
        player.openInventory(inventory);
        onOpen(player);
    }

    public void open(Player player) {
        open(player, true);
    }

    public void applyInventory(Player player) {
        setItems(player, player.getInventory());
    }
}
