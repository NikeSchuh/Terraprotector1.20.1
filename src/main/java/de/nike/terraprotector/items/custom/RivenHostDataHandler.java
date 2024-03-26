package de.nike.terraprotector.items.custom;

import com.mojang.datafixers.util.Pair;
import de.nike.terraprotector.stats.RivenStat;
import de.nike.terraprotector.stats.RivenStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.List;

public class RivenHostDataHandler {

    public static String TAG_EQUIPPED_RIVEN = "rivenInventory";
    public static String TAG_STATS = "combinedStats";

    public static boolean checkHost(ItemStack stack) {
        return stack.getItem() instanceof IRivenSlotProvider;
    }

    public static void updateRivenStatsData(ItemStackHandler rivenInventory, ItemStack rivenHost) {
            CompoundTag rivenStats = new CompoundTag();
            HashMap<RivenStat<?>, Object> totalStats = new HashMap<>();
            for(int slot = 0; slot < rivenInventory.getSlots(); slot++){
                ItemStack riven = rivenInventory.getStackInSlot(slot);
                if(riven == ItemStack.EMPTY) continue;
                if(!(riven.getItem() instanceof RivenItem)) continue;
                System.out.println("Found riven in slot " + slot);
                List<Pair<RivenStat<?>, Object>> list = RivenItem.getRivenStats(riven);
                if(list.isEmpty()) continue;
                for(Pair<RivenStat<?>, Object> stat : list) {
                    System.out.println("Found riven stat " + stat.getFirst().getRegistryName());
                    if(!totalStats.containsKey(stat.getFirst())) {
                        System.out.println("Saving " + stat.getFirst().getRegistryName() + " to total stats");
                        totalStats.put(stat.getFirst(), stat.getSecond());
                    } else {
                        System.out.println("Combining and saving " + stat.getFirst().getRegistryName() + " to total stats");
                        Object currentValue = totalStats.get(stat.getFirst());
                        totalStats.put(stat.getFirst(), stat.getFirst().getDataType().combine(currentValue, stat.getSecond()));
                    }
                }

            }

            for(RivenStat<?> stat : totalStats.keySet()) {
                Object value = totalStats.get(stat);
                stat.getDataType().serialize(rivenStats, stat.getRegistryName(), value);
            }

            CompoundTag itemNbt = rivenHost.getOrCreateTag();
            itemNbt.put(TAG_STATS, rivenStats);
            RivenStats.cache.remove(rivenHost);
            rivenHost.setTag(itemNbt);
    }

}
