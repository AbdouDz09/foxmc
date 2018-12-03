package me.rellynn.foxmc.hub.features.cosmetics.pets;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.cosmetics.pets.types.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 21/05/2017.
 * FoxMC Network.
 */
public class PetsMenu extends VirtualMenu {

    public PetsMenu() {
        super("pets", "Features > Pets");
        addItemToDisplay(getSlot(5, 1), new VirtualItem("kill_pet") {
            private final ItemStack ITEM = new ItemBuilder(Material.BARRIER)
                    .setTitle("§cKill Pet")
                    .build();

            @Override
            protected void onClick(Player player, PerformedAction action) {
                if (!HubAPI.get().getPetsManager().removePet(player)) {
                    player.sendMessage("§cYou don't have any pet!");
                } else {
                    player.sendMessage("§cPet removed!");
                    player.closeInventory();
                }
            }

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(2, 2), new ChickenPet());
        addItemToDisplay(getSlot(3, 2), new CowPet());
        addItemToDisplay(getSlot(4, 2), new MushroomCowPet());
        addItemToDisplay(getSlot(5, 2), new SlimePet());
        addItemToDisplay(getSlot(6, 2), new MagmaCubePet());
        addItemToDisplay(getSlot(7, 2), new OcelotPet());
        addItemToDisplay(getSlot(8, 2), new WolfPet());
        addItemToDisplay(getSlot(2, 3), new RabbitPet());
        addItemToDisplay(getSlot(3, 3), new BatPet());
        addItemToDisplay(getSlot(4, 3), new ZombiePet());
        addItemToDisplay(getSlot(5, 3), new SheepBat());
        addItemToDisplay(getSlot(6, 3), new AdventurerPet());
        addItemToDisplay(getSlot(7, 3), new LuckyBlockPet());
        addItemToDisplay(getSlot(8, 3), new FoxPet());
        addItemToDisplay(getSlot(2, 4), new SaplingPet());
        addItemToDisplay(getSlot(5, 5), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 5), HubItems.backMenuItem);
    }
}
