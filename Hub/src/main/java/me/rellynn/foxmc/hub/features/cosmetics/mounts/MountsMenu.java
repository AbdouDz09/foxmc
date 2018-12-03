package me.rellynn.foxmc.hub.features.cosmetics.mounts;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.items.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 20/06/2017.
 * FoxMC Network.
 */
public class MountsMenu extends VirtualMenu {

    public MountsMenu() {
        super("mounts", "Features > Mounts");
        addItemToDisplay(getSlot(5, 1), new VirtualItem("kill_mount") {
            private final ItemStack ITEM = new ItemBuilder(Material.BARRIER)
                    .setTitle("§cKill Mount")
                    .build();

            @Override
            protected void onClick(Player player, PerformedAction action) {
                if (!player.isInsideVehicle()) {
                    player.sendMessage("§cYou don't have any mount!");
                } else {
                    player.leaveVehicle();
                    player.sendMessage("§cYour mount has been killed!");
                    player.closeInventory();
                }
            }

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(2, 2), new CowMount());
        addItemToDisplay(getSlot(3, 2), new WhiteHorseMount());
        addItemToDisplay(getSlot(4, 2), new BrownHorseMount());
        addItemToDisplay(getSlot(5, 2), new MuleMount());
        addItemToDisplay(getSlot(6, 2), new UndeadHorseMount());
        addItemToDisplay(getSlot(7, 2), new SkeletonHorseMount());
        addItemToDisplay(getSlot(8, 2), new PigMount());
        addItemToDisplay(getSlot(2, 3), new OcelotMount());
        addItemToDisplay(getSlot(3, 3), new WolfMount());
        addItemToDisplay(getSlot(4, 3), new DiscoSheepMount());
        addItemToDisplay(getSlot(5, 3), new SlimeMount());
        addItemToDisplay(getSlot(6, 3), new SnakeMount());
        addItemToDisplay(getSlot(7, 3), new SpiderMount());
        addItemToDisplay(getSlot(8, 3), new CaveSpiderMount());
        addItemToDisplay(getSlot(2, 4), new RabbitMount());
        addItemToDisplay(getSlot(3, 4), new SquidMount());
        addItemToDisplay(getSlot(4, 4), new ChickenMount());
        addItemToDisplay(getSlot(5, 4), new GuardianMount());
        addItemToDisplay(getSlot(5, 5), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 5), HubItems.backMenuItem);
    }
}
