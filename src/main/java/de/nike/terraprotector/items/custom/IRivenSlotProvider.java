package de.nike.terraprotector.items.custom;

import de.nike.terraprotector.stats.RivenHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IRivenSlotProvider {

    int getSlots(ItemStack stack);
    List<RivenHost> getHostList();


}
