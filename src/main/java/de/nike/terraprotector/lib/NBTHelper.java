package de.nike.terraprotector.lib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.UUID;

public class NBTHelper {


    public static String getString(ItemStack stack, String tag, String fallback) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getString(tag);
        return fallback;
    }

    public static void setString(ItemStack stack, String tag, String value) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putString(tag, value);
    }

    public static void setUUID(ItemStack stack, String tag, UUID value) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putUUID(tag, value);
    }

    public static UUID getUUID(ItemStack stack, String tag, @Nullable UUID fallback) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getUUID(tag);
        return fallback;
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean b) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getBoolean(tag);
        return b;
    }

    public static void setBoolean(ItemStack stack, String tag, boolean b) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putBoolean(tag, b);
    }

    public static int getInt(ItemStack stack, String tag, int defaultValue) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getInt(tag);
        return defaultValue;
    }

    public static BigInteger getBigInt(ItemStack stack, String tag, int defaultValue) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return new BigInteger(compoundNBT.getString(tag));
        return BigInteger.valueOf(defaultValue);
    }

    public static void setBigInteger(ItemStack stack, String tag, BigInteger bigInteger) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putString(tag, bigInteger.toString());
    }

    public static double getDouble(ItemStack stack, String tag, double defaultValue) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getDouble(tag);
        return defaultValue;
    }

    public static void setDouble(ItemStack stack, String tag, double value) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putDouble(tag, value);
    }

    public static void setInt(ItemStack stack, String tag, int value) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putInt(tag, value);
    }

    public static float getFloat(ItemStack stack, String tag, float defaultValue) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        if(compoundNBT.contains(tag)) return compoundNBT.getFloat(tag);
        return defaultValue;
    }

    public static void setFloat(ItemStack stack, String tag, float value) {
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putFloat(tag, value);
    }
}
