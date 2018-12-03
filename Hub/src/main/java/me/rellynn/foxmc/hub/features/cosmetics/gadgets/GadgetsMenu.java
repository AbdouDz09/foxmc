package me.rellynn.foxmc.hub.features.cosmetics.gadgets;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.types.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 19/05/2017.
 * FoxMC Network.
 */
public class GadgetsMenu extends VirtualMenu {

    public GadgetsMenu() {
        super("gadgets", "Features > Gadgets");
        addItemToDisplay(getSlot(5, 1), new VirtualItem("remove_gadget") {
            private final ItemStack ITEM = new ItemBuilder(Material.BARRIER)
                    .setTitle("§cRemove Gadget")
                    .build();

            @Override
            protected void onClick(Player player, PerformedAction action) {
                if (!HubAPI.get().getGadgetsManager().removeGadget(player)) {
                    player.sendMessage("§cYou don't have any gadget enabled!");
                } else {
                    player.getInventory().clear(3);
                    player.sendMessage("§cGadget removed!");
                    player.closeInventory();
                }
            }

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(2, 2), new CloudGadget());
        addItemToDisplay(getSlot(3, 2), new CowBalloonGadget());
        addItemToDisplay(getSlot(4, 2), new ParachuteGadget());
        addItemToDisplay(getSlot(5, 2), new RainbowTrailGadget());
        addItemToDisplay(getSlot(6, 2), new SuicidalSheepGadget());
        addItemToDisplay(getSlot(7, 2), new SmokeEscapeGadget());
        addItemToDisplay(getSlot(8, 2), new AntiGravityGadget());
        addItemToDisplay(getSlot(2, 3), new JetpackGadget());
        addItemToDisplay(getSlot(5, 4), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
    }
}
