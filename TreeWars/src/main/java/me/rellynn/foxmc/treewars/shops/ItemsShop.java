package me.rellynn.foxmc.treewars.shops;

import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.treewars.shops.submenus.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 30/05/2017.
 * FoxMC Network.
 */
public class ItemsShop extends VirtualMenu {

    public ItemsShop() {
        super("shop", "Items Shop");
        addItemToDisplay(1, new VirtualItem("shop_blocks", VirtualItem.ActionType.OPEN_MENU, new BlocksMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.SANDSTONE)
                    .setTitle("§fBlocks")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Glass")
                    .addLore(" §8- §7Clay")
                    .addLore(" §8- §7End Stone")
                    .addLore(" §8- §7Wood Planks")
                    .addLore(" §8- §7Ladder")
                    .addLore(" §8- §7Obsidian")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(2, new VirtualItem("shop_weapons", VirtualItem.ActionType.OPEN_MENU, new WeaponsMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.GOLD_SWORD)
                    .setTitle("§fWeapons")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Stone Sword")
                    .addLore(" §8- §7Iron Sword")
                    .addLore(" §8- §7Diamond Sword")
                    .addLore(" §8- §7Feather Knockback 2")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .setAttributes(false)
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(3, new VirtualItem("shop_armour", VirtualItem.ActionType.OPEN_MENU, new ArmourMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.IRON_CHESTPLATE)
                    .setTitle("§fArmour")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Leather Chestplate")
                    .addLore(" §8- §7Chain Chestplate")
                    .addLore(" §8- §7Iron Chestplate")
                    .addLore(" §8- §7Diamond Chestplate")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .setAttributes(false)
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(4, new VirtualItem("shop_tools", VirtualItem.ActionType.OPEN_MENU, new ToolsMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.DIAMOND_PICKAXE)
                    .setTitle("§fTools")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Stone Pickaxe")
                    .addLore(" §8- §7Iron Pickaxe")
                    .addLore(" §8- §7Diamond Pickaxe §8(Efficiency III)")
                    .addLore(" §8- §7Iron Axe §8(Efficiency II)")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .setAttributes(false)
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(5, new VirtualItem("shop_archery", VirtualItem.ActionType.OPEN_MENU, new ArcheryMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.BOW)
                    .setTitle("§fArchery")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Bow")
                    .addLore(" §8- §7Arrow")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(6, new VirtualItem("shop_food", VirtualItem.ActionType.OPEN_MENU, new FoodMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.COOKED_BEEF)
                    .setTitle("§fFood")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Carrot")
                    .addLore(" §8- §7Cooked Beef")
                    .addLore(" §8- §7Cake")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
        addItemToDisplay(7, new VirtualItem("shop_misc", VirtualItem.ActionType.OPEN_MENU, new MiscMenu().getId()) {
            private final ItemStack ITEM = new ItemBuilder(Material.CHEST)
                    .setTitle("§fMiscellaneous")
                    .addLore("§8Available:")
                    .addLore(" §8- §7Golden Apple")
                    .addLore(" §8- §7TNT")
                    .addLore(" §8- §7Ender Pearl")
                    .addLore(" §8- §7Snowball")
                    .addLore("")
                    .addLore("§eClick to browse!")
                    .build();

            @Override
            public ItemStack getItem(Player player) {
                return ITEM;
            }
        });
    }
}
