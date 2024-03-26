package de.nike.terraprotector.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import de.nike.terraprotector.client.render.IBarRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class ToolTipRendererClient implements ClientTooltipComponent {

    private int mouseX, mouseY;
    private int totalWidth;
    private IBarRenderer renderer;
    private ItemStack stack;

    public ToolTipRendererClient(TooltipBarRenderer component) {
        this.renderer = component.getRenderer();
        this.stack = component.getStack();
    }

    public void setContext(int mouseX, int mouseY, int totalWidth) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.totalWidth = totalWidth;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(Font p_169952_) {
        return 0;
    }


    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        ClientTooltipComponent.super.renderText(font, x, y, matrix, source);
        renderer.renderTooltipText(stack, x, y-16, totalWidth, 3, font, matrix ,source);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        ClientTooltipComponent.super.renderImage(font, x, y, graphics);
    }

}
