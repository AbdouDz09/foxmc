package me.rellynn.foxmc.bukkitapi.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class ItemBuilder {
    private static Class<?> metaSkullClass = Reflection.getCraftBukkitClass("inventory.CraftMetaSkull");
    private static Reflection.FieldAccessor<GameProfile> profileField = Reflection.getField(metaSkullClass, "profile", GameProfile.class);

    public static ItemStack getPlayerSkull(String hash) {
        String url = "http://textures.minecraft.net/texture/" + hash;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = org.apache.commons.codec.binary.Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        return new ItemBuilder(org.bukkit.Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal()).setSkullProfile(profile).build();
    }

    private Material type;
    private int amount;
    private short damage;
    private String title;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private String skullOwner;
    private GameProfile skullProfile;
    private Color color;
    private boolean attributes = true;
    private boolean unbreakable;
    private boolean glow;

    public ItemBuilder(Material type, int amount, short damage) {
        this.type = type;
        this.amount = amount;
        this.damage = damage;
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
    }

    public ItemBuilder(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                title = meta.getDisplayName();
            }
            if (meta.hasEnchants()) {
                enchantments.putAll(meta.getEnchants());
            }
            if (meta.hasLore()) {
                lore.addAll(meta.getLore());
            }
            if (meta instanceof SkullMeta) {
                skullProfile = profileField.get(meta);
            }
            if (meta instanceof LeatherArmorMeta) {
                color = ((LeatherArmorMeta) meta).getColor();
            }
            attributes = !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
            unbreakable = meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
            glow = meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
        }
    }

    public ItemBuilder(Material type) {
        this(type, 1, (short) 0);
    }

    public ItemBuilder(Material type, int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(Material type, short damage) {
        this(type, 1, damage);
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        Collections.addAll(this.lore, lore);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public ItemBuilder setSkullProfile(GameProfile profile) {
        this.skullProfile = profile;
        return this;
    }

    public ItemBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ItemBuilder setAttributes(boolean attributes) {
        this.attributes = attributes;
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(type, amount, damage);
        if (!enchantments.isEmpty()) {
            item.addUnsafeEnchantments(enchantments);
        }
        ItemMeta meta = item.getItemMeta();
        if (title != null) {
            meta.setDisplayName(title);
        }
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        if (meta instanceof SkullMeta) {
            if (skullProfile != null) {
                profileField.set(meta, skullProfile);
            } else if (skullOwner != null) {
                ((SkullMeta) meta).setOwner(skullOwner);
            }
        }
        if (color != null && meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }
        if (!attributes) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        if (glow) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            if (enchantments.isEmpty()) {
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            }
        }
        if (unbreakable) {
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        item.setItemMeta(meta);
        return item;
    }
}
