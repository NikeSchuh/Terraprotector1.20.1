package de.nike.terraprotector.items.custom;

import de.nike.terraprotector.client.ClientTickHandler;
import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import de.nike.terraprotector.lib.ColorUtil;
import de.nike.terraprotector.lib.TextUtils;
import de.nike.terraprotector.stats.RivenHost;
import de.nike.terraprotector.stats.RivenStat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TerraProtectorItem extends Item implements IBarRenderer, ICurioItem, IRivenSlotProvider {
    public TerraProtectorItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void renderTooltipText(ItemStack stack, int tooltipX, int tooltipY, int width, int height, Font font, Matrix4f matrix, MultiBufferSource.BufferSource source) {

    }

    @Override
    public void renderTooltipImage(ItemStack stack, int tooltipX, int tooltipY, int width, int height, GuiGraphics graphics) {

    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if(stack.getItem() instanceof IBarRenderer) {
            return Optional.of(new TooltipBarRenderer((IBarRenderer) stack.getItem(), stack));
        } else {
            throw new IllegalArgumentException("Not here");
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> list, TooltipFlag p_41424_) {
        super.appendHoverText(stack, p_41422_, list, p_41424_);
        if (RivenItem.hasModifiers(stack)) {
            HashMap<RivenStat<?>, Object> stats = RivenItem.getRivenStatsMap(stack);

            for (RivenStat<?> stat : stats.keySet()) {
                Object value = stats.get(stat);
                list.add(TextUtils.string(ColorUtil.rainbowWaveTextEffect(stat.getStatFormatter().format(value) + " " + TextUtils.translation("terraprotector.stats." + stat.getRegistryName()), ClientTickHandler.ticksInGame, 100.0F)));
            }

        }
    }

    @Override
    public int getSlots(ItemStack stack) {
        return 16;
    }

    @Override
    public List<RivenHost> getHostList() {
        return Arrays.asList(RivenHost.PROTECTOR, RivenHost.MANA_USER);
    }
}
