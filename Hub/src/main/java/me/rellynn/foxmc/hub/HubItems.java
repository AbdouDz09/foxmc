package me.rellynn.foxmc.hub;

import me.rellynn.foxmc.api.players.Rank;
import me.rellynn.foxmc.api.players.database.PlayerData;
import me.rellynn.foxmc.api.settings.SettingValue;
import me.rellynn.foxmc.api.settings.Settings;
import me.rellynn.foxmc.bukkitapi.api.FoxAPI;
import me.rellynn.foxmc.bukkitapi.utils.ItemBuilder;
import me.rellynn.foxmc.bukkitapi.utils.VirtualItem;
import me.rellynn.foxmc.hub.api.HubAPI;
import me.rellynn.foxmc.hub.features.FeaturesMenu;
import me.rellynn.foxmc.hub.features.cosmetics.effects.EffectsMenu;
import me.rellynn.foxmc.hub.features.cosmetics.gadgets.GadgetsMenu;
import me.rellynn.foxmc.hub.features.cosmetics.mounts.MountsMenu;
import me.rellynn.foxmc.hub.features.cosmetics.pets.PetsMenu;
import me.rellynn.foxmc.hub.features.settings.PlayerSettings;
import me.rellynn.foxmc.hub.hotbar.selector.HubSelector;
import me.rellynn.foxmc.hub.main.MainMenu;
import me.rellynn.foxmc.hub.shops.ShopsMenu;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by gwennaelguich on 19/08/2017.
 * FoxMC Network.
 */
public class HubItems {
    public static VirtualItem backMenuItem = new VirtualItem("back", VirtualItem.ActionType.BACK) {
        private final ItemStack ITEM = new ItemBuilder(Material.ARROW)
                .setTitle("§rGo back")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem visibilityItem = new VirtualItem("visibility_item", VirtualItem.ActionType.CUSTOM, 3) {
        private final ItemStack VISIBLE_ITEM = new ItemBuilder(Material.EYE_OF_ENDER)
                .setTitle("§6Player Visibility §7<§aON§7>")
                .addLore("§aUse it to:")
                .addLore("§7- hide players")
                .addLore("§7- hide particles")
                .addLore("§7- remove sounds")
                .build();
        private final ItemStack HIDDEN_ITEM = new ItemBuilder(Material.ENDER_PEARL)
                .setTitle("§6Player Visibility §7<§cOFF§7>")
                .addLore("§aUse it to:")
                .addLore("§7- hide players")
                .addLore("§7- hide particles")
                .addLore("§7- remove sounds")
                .build();

        @Override
        public void onClick(Player player, PerformedAction action) {
            boolean newState = Settings.hubVisibility.toggle(FoxAPI.getPlayer(player));
            player.sendMessage(!newState ? "§cPlayer Visibility disabled." : "§aPlayer Visibility enabled.");
            player.playSound(player.getEyeLocation(), Sound.FIREWORK_BLAST2, 1.0F, 1.0F);
            player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(90, 0));
            HubAPI.get().getVisibilityManager().updateVisibility(player);
            setItem(player, player.getInventory(), 2);
        }

        @Override
        public ItemStack getItem(Player player) {
            return Settings.hubVisibility.is(FoxAPI.getPlayer(player), SettingValue.ENABLED) ? VISIBLE_ITEM : HIDDEN_ITEM;
        }
    };
    public static VirtualItem playerInfoItem = new VirtualItem("player_info", VirtualItem.ActionType.NOTHING) {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData data = FoxAPI.getPlayer(player);
            Rank rank = data.getRank();
            return new ItemBuilder(Material.SKULL_ITEM, (short) SkullType.PLAYER.ordinal())
                    .setSkullProfile(((CraftPlayer) player).getProfile()) // Use the profile to prevent a new skin loading
                    .setTitle("§f§n" + player.getName())
                    .addLore("§7Rank: " + rank.getName())
                    .addLore("§7FoxCoins: §6" + data.getCoins())
                    .addLore("§7Tails: §a" + data.getTails())
                    .addLore("§7Booster: §e" + (rank.getCoinsBooster() * 100) + "%")
                    .build();
        }
    };
    public static VirtualItem spawnItem = new VirtualItem("spawn", VirtualItem.ActionType.CUSTOM) {
        private final ItemStack ITEM = new ItemBuilder(Material.BED)
                .setTitle("§aSpawn")
                .addLore("§7Click to go to spawn")
                .build();

        @Override
        protected void onClick(Player player, PerformedAction action) {
            player.teleport(HubConfig.spawnLocation);
        }

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem shopsItem = new VirtualItem("shops", VirtualItem.ActionType.OPEN_MENU, new ShopsMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.EMERALD)
                .setTitle("§eShops")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem mainItem = new VirtualItem("main", VirtualItem.ActionType.OPEN_MENU, new MainMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.COMPASS)
                .setTitle("§6Main Menu")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem selectorItem = new VirtualItem("hub_selector", VirtualItem.ActionType.OPEN_MENU, new HubSelector().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
                .setTitle("§aHub Selector")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem settingsItem = new VirtualItem("settings", VirtualItem.ActionType.OPEN_MENU, new PlayerSettings().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .setTitle("§aSettings")
                .addLore("§7Manage your settings")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem gadgetsItem = new VirtualItem("gadgets", VirtualItem.ActionType.OPEN_MENU, new GadgetsMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.BLAZE_ROD)
                .setTitle("§aGadgets")
                .addLore("§7Have fun with other players!")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem petsItem = new VirtualItem("pets", VirtualItem.ActionType.OPEN_MENU, new PetsMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.MONSTER_EGG, 1, (byte) EntityType.values()[(int) (Math.random() * EntityType.values().length)].getTypeId())
                .setTitle("§aPets")
                .addLore("§7Choose a companion to follow")
                .addLore("§7you everywhere you go!")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem mountsItem = new VirtualItem("mounts", VirtualItem.ActionType.OPEN_MENU, new MountsMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.SADDLE)
                .setTitle("§aMounts")
                .addLore("§7Use the most appropriate mount")
                .addLore("§7for your rides!")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem effectsItem = new VirtualItem("effects", VirtualItem.ActionType.OPEN_MENU, new EffectsMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.BLAZE_POWDER)
                .setTitle("§aEffects")
                .addLore("§7Play cool animations.")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
    public static VirtualItem featuresItem = new VirtualItem("features", VirtualItem.ActionType.OPEN_MENU, new FeaturesMenu().getId()) {
        private final ItemStack ITEM = new ItemBuilder(Material.ENDER_CHEST)
                .setTitle("§bFeatures")
                .build();

        @Override
        public ItemStack getItem(Player player) {
            return ITEM;
        }
    };
}
