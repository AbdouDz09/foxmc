package me.rellynn.foxmc.hub.features.cosmetics.mounts.menus;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public class SlimeSizeMenu extends VirtualMenu {

    public SlimeSizeMenu() {
        super("slime_mount_menu", "Slime Size");
        addItemToDisplay(3, new SlimeSizeItem(1));
        addItemToDisplay(4, new SlimeSizeItem(2));
        addItemToDisplay(5, new SlimeSizeItem(3));
    }

    class SlimeSizeItem extends VirtualItem {
        private ItemStack item;
        private int size;

        SlimeSizeItem(int size) {
            super("slime_mount_" + size, ActionType.CUSTOM, "");
            this.item = new ItemBuilder(Material.SLIME_BALL, size).setTitle("§aSize §b" + size).build();
            this.size = size;
        }

        @Override
        protected void onClick(Player player, PerformedAction action) {
            player.sendMessage("§aMount spawned!");
            player.closeInventory();
            MountsManager.spawnSlimeMount(player, size);
        }

        @Override
        public ItemStack getItem(Player player) {
            return item;
        }
    }
}
