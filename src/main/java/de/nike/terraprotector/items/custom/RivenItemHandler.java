package de.nike.terraprotector.items.custom;

import de.nike.terraprotector.client.screens.RivenSlotInfo;
import de.nike.terraprotector.client.screens.RivenTab;
import de.nike.terraprotector.client.screens.RivenTabRenderType;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class RivenItemHandler {

    public static List<ItemStack> getRivenHostItems(Player player) {
        List<ItemStack> allItems = new ArrayList<>();
        player.getInventory().items.forEach(itemStack ->{
            if(itemStack != null && itemStack.getItem() instanceof IRivenSlotProvider) {
                allItems.add(itemStack);
            }
        });
        CuriosApi.getCuriosInventory(player).ifPresent(s -> {
            int slotAmount = s.getEquippedCurios().getSlots();
            for(int slot = 0; slot < slotAmount; slot++) {
                ItemStack stack = s.getEquippedCurios().getStackInSlot(slot);
                if(stack != null && stack.getItem() instanceof IRivenSlotProvider) allItems.add(stack);
            }
        });
        return allItems;
    }

    public static boolean hasRivenHostProviders(Player player) {
        System.out.println(getRivenHostItems(player).size());
        return getRivenHostItems(player).size() > 0;
    }

}
