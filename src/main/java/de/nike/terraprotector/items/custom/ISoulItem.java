package de.nike.terraprotector.items.custom;

import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ISoulItem {

    float getCurrentSouls(ItemStack stack);
    void modifyCurrentSouls(ItemStack stack, float value);
    float getMaximumSouls(ItemStack stack, float value);
    boolean canAcceptSouls(ItemStack stack);



}
