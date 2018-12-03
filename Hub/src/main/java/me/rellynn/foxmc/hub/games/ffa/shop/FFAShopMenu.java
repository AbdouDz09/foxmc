package me.rellynn.foxmc.hub.games.ffa.shop;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.games.ffa.shop.aura.AuraShopMenu;
import me.rellynn.foxmc.hub.games.ffa.shop.effects.EffectsShopMenu;
import me.rellynn.foxmc.hub.games.ffa.shop.sounds.SoundsShopMenu;
import me.rellynn.foxmc.hub.games.ffa.shop.upgrades.UpgradesShopMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 18/06/2017.
 * FoxMC Network.
 */
public class FFAShopMenu extends VirtualMenu {

    public FFAShopMenu() {
        super("ffa_shop", "FFA Shop");
        addItemToDisplay(getSlot(2, 2), new VirtualItem("ffa_upgrades", VirtualItem.ActionType.OPEN_MENU, new UpgradesShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.BLAZE_ROD)
                    .setTitle("§cUpgrades")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(4, 2), new VirtualItem("ffa_effects", VirtualItem.ActionType.OPEN_MENU, new EffectsShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.FIREWORK)
                    .setTitle("§cEffects")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(6, 2), new VirtualItem("ffa_sounds", VirtualItem.ActionType.OPEN_MENU, new SoundsShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.GOLD_RECORD)
                    .setTitle("§cKilling Sounds")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(8, 2), new VirtualItem("ffa_aura", VirtualItem.ActionType.OPEN_MENU, new AuraShopMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.ENDER_CHEST)
                    .setTitle("§cAura")
                    .addLore("§7Gives you an aura for §e10 seconds")
                    .addLore("§7when you get a killstreak!")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(getSlot(1, 4), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
    }
}
