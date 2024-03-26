package de.nike.terraprotector.lib;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryUtil {

    public static ItemStackHandler createVirtualInventory(int slots, String tag, ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(slots);
        CompoundTag nbt = stack.getOrCreateTag();
        handler.deserializeNBT(nbt.getCompound(tag));
        NonNullList<ItemStack> stacks = NonNullList.withSize(slots, ItemStack.EMPTY);

        for(int slot = 0; slot < handler.getSlots(); slot++) {
            stacks.set(slot, handler.getStackInSlot(slot));
        }

        return new ItemStackHandler(stacks);
    }

    public static void serializeInventory(ItemStackHandler itemHandler, String tag, ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.put(tag, itemHandler.serializeNBT());
        stack.setTag(nbt);
    }
}
