package de.nike.terraprotector.client.tooltip;

import de.nike.terraprotector.client.render.IBarRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class TooltipBarRenderer implements TooltipComponent {

    private final IBarRenderer renderer;
    private final ItemStack stack;


    public TooltipBarRenderer(IBarRenderer renderer, ItemStack stack) {
        this.renderer = renderer;
        this.stack  =stack;
    }

    public IBarRenderer getRenderer() {
        return renderer;
    }

    public ItemStack getStack() {
        return stack;
    }
}
