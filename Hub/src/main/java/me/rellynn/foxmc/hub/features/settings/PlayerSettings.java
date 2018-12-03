package me.rellynn.foxmc.hub.features.settings;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.settings.Setting;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.utils.VirtualMenu;
import me.rellynn.foxmc.hub.HubItems;
import me.rellynn.foxmc.hub.features.settings.items.SettingItem;
import me.rellynn.foxmc.hub.features.settings.items.SettingStateItem;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 14/08/2017.
 * FoxMC Network.
 */
public class PlayerSettings extends VirtualMenu {

    public PlayerSettings() {
        super("settings", "⚙ Settings");
        addSetting(0, new ItemStack(Material.COMMAND), Settings.hubProtection, "Hub Protection", new String[]{"§7Toggles whether or not you", "§7need to type §e/hub §7twice", "§7to avoid accidents."});
        addSetting(2, new ItemStack(Material.PAPER), Settings.privateMessages, "Private Messages", new String[]{"§7Choose who can send you", "§7private messages."});
        addSetting(4, new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()), Settings.friendRequests, "Friend Requests", new String[]{"§7Allows players to send you", "§7friend requests."});
        addSetting(6, new ItemStack(Material.CAKE), Settings.partyInvites, "Party Invites", new String[]{"§7Manages who can send you", "§7party invites."});
        addSetting(8, new ItemStack(Material.FEATHER), Settings.hubFly, "Fly in hubs", new String[]{"§7You must be " + Rank.VIP.getName() + " §7or higher!"});
        addItemToDisplay(getSlot(5, 4), HubItems.playerInfoItem);
        addItemToDisplay(getSlot(9, 4), HubItems.backMenuItem);
    }

    private void addSetting(int slot, ItemStack item, Setting setting, String title, String[] description) {
        addItemToDisplay(slot, new SettingItem(item, setting, title, description));
        addItemToDisplay(slot + 9, new SettingStateItem(setting));
    }
}
