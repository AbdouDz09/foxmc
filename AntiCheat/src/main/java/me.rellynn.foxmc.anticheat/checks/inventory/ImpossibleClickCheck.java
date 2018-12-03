package me.rellynn.foxmc.anticheat.checks.inventory;

import me.rellynn.foxmc.anticheat.checks.Check;
import me.rellynn.foxmc.anticheat.data.ACPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

/**
 * Created by gwennaelguich on 22/07/2017.
 * FoxMC Network.
 */
public class ImpossibleClickCheck extends Check {

    public ImpossibleClickCheck() {
        super("ImpossibleClick", 10);
    }

    public void check(Player player, ACPlayer acPlayer, ClickType clickType, InventoryAction action, Inventory inventory) {
        boolean failed = inventory != null && !acPlayer.inventoryOpened && action != InventoryAction.NOTHING && !clickType.isCreativeAction() && !clickType.isKeyboardClick() && clickType != ClickType.DROP;
        handleActions(failed, acPlayer);
    }
}
