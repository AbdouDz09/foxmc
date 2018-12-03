package me.rellynn.foxmc.bukkitapi.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by gwennaelguich on 02/04/2017.
 * FoxMC Network.
 */
public class ItemUtils {

    public static net.minecraft.server.v1_8_R3.ItemStack fromItemStack(ItemStack stack) {
        return CraftItemStack.asNMSCopy(stack);
    }

    public static NBTTagCompound getTag(net.minecraft.server.v1_8_R3.ItemStack stack) {
        if (stack == null) {
            return null;
        }
        NBTTagCompound compound = stack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        return compound;
    }

    public static ItemStack setString(ItemStack stack, String key, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = fromItemStack(stack);
        NBTTagCompound compound = getTag(nmsStack);
        compound.setString(key, value);
        nmsStack.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static String getString(ItemStack stack, String key) {
        NBTTagCompound compound = getTag(fromItemStack(stack));
        if (compound == null) {
            return "";
        }
        return compound.getString(key);
    }
}
