package me.rellynn.foxmc.bukkitapi.utils;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
@Getter
public class MenuHolder implements InventoryHolder {
    private VirtualMenu menu;

    MenuHolder(VirtualMenu menu) {
        this.menu = menu;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
