package me.rellynn.foxmc.hub.features.cosmetics.effects;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.effects.subtypes.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 21/06/2017.
 * FoxMC Network.
 */
public class EffectsMenu extends VirtualMenu {

    public EffectsMenu() {
        super("effects", "Features > Effects");
        addItemToDisplay(getSlot(5, 1), new VirtualItem("stop_effect") {
            private final ItemStack ITEM = new ItemBuilder(Material.BARRIER)
                    .setTitle("§cStop Effect")
                    .build();

            @Override
            protected void onClick(Player player, PerformedAction action) {
                if (!HubAPI.get().getEffectsManager().stopEffect(player)) {
                    player.sendMessage("§cYou don't have any effect started!");
                } else {
                    player.sendMessage("§cYour effect has been stopped!");
                    player.closeInventory();
                }
            }

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(2, 2), new BurningEffect());
        addItemToDisplay(getSlot(3, 2), new SingerEffect());
        addItemToDisplay(getSlot(4, 2), new FlameCrownEffect());
        addItemToDisplay(getSlot(5, 2), new AngryEffect());
        addItemToDisplay(getSlot(6, 2), new LoveEffect());
        addItemToDisplay(getSlot(7, 2), new WaterCrownEffect());
        addItemToDisplay(getSlot(8, 2), new EndermanEffect());
        addItemToDisplay(getSlot(2, 3), new BleedingEffect());
        addItemToDisplay(getSlot(5, 4), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
    }
}
