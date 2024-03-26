package de.nike.terraprotector.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.nike.terraprotector.client.ClientTickHandler;
import de.nike.terraprotector.lib.ColorUtil;
import de.nike.terraprotector.lib.NikesMath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import vazkii.botania.api.BotaniaRegistries;

import java.awt.*;

public interface IBarRenderer {

    void renderTooltipText(ItemStack stack, int tooltipX, int tooltipY, int width, int height, Font font, Matrix4f matrix, MultiBufferSource.BufferSource source);
    void renderTooltipImage(ItemStack stack, int tooltipX, int tooltipY, int width, int height, GuiGraphics graphics);

    default void drawSimpleBar(Matrix4f matrix, int mouseX, int mouseY, int width, int height, int barSlot, float percent, int fillColor) {
        int rainbowWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));
        BufferBuilder builder = RenderHelper.setupRectangle();
        RenderHelper.rectangle(builder,matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        RenderHelper.rectangle(builder,matrix, mouseX, mouseY - height, mouseX + rainbowWidth, mouseY, fillColor);
        RenderHelper.rectangle(builder,matrix,  mouseX + rainbowWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);
    }

    default void drawSimpleBarWithFade(Matrix4f matrix, int mouseX, int mouseY, int width, int height, int barSlot, float percent, Color startColor, Color endColor) {
        int fillWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));
        BufferBuilder builder = RenderHelper.setupRectangle();
        RenderHelper.rectangle(builder, matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder, matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, ColorUtil.fade(startColor, endColor, ((double) i) / fillWidth));
        }
        RenderHelper.rectangle(builder, matrix, mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);
    }

    default void drawSimpleBarWithFadeTextAndRainbow(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent, Font font, String startText, String endText) {
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01F;
        Color start = Color.getHSBColor(hueOff, 1F, 1F);
        Color end = Color.getHSBColor(1 - hueOff, 1F, 1F);
        int fillWidth = Math.min(width, (int) (width * percent));

        BufferBuilder builder = RenderHelper.setupRectangle();

        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));
        RenderHelper.rectangle(builder, matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder, matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, ColorUtil.fade(start, end, ((double) i) / fillWidth));
        }
        RenderHelper.rectangle(builder, matrix, mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);
        if(startText != null || endText != null) {
            if(startText != null) {
                font.drawInBatch(startText, mouseX, mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);
            }
            if (endText != null) {
                font.drawInBatch(endText, mouseX + width - font.width(endText), mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);

            }
        }
    }

   default void drawSimpleBarWithFadeWaveText(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent, int startColor, int endColor, float speedMulti, Font font, String startText, String endText) {
        int fillWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));

        float time = ClientTickHandler.ticksInGame / 20.0F;
        time*=speedMulti;

        BufferBuilder builder = RenderHelper.setupRectangle();

        RenderHelper.rectangle(builder,matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder,matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, ColorUtil.fastFade(startColor, endColor, (Math.sin(((double) i / fillWidth) + ((Math.sin(time))) / 2) + 1) / 2));
        }
        RenderHelper.rectangle(builder,matrix, mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);

        RenderHelper.endRectangle(builder);

       if(startText != null || endText != null) {
           if(startText != null) {
               font.drawInBatch(startText, mouseX, mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);
           }
           if (endText != null) {
               font.drawInBatch(endText, mouseX + width - font.width(endText), mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);

           }
       }
    }


    default void drawSimpleBarWithFadeAndRainbow(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent) {
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01F;
        Color start = Color.getHSBColor(hueOff, 1F, 1F);
        Color end = Color.getHSBColor(1 - hueOff, 1F, 1F);
        int fillWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));
        BufferBuilder builder = RenderHelper.setupRectangle();
        RenderHelper.rectangle(builder, matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder, matrix,  mouseX + i, mouseY - height, mouseX + i + 1, mouseY, ColorUtil.fade(start, end, ((double) i) / fillWidth));
        }
        RenderHelper.rectangle(builder, matrix,  mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);
    }

    default void drawSimpleBarWithRainbowWaveAndText(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent, Font font, String startText, String endText) {
        float huePer = width == 0 ? 0F : 1F / width;
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01F;
        int fillWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));

        BufferBuilder builder = RenderHelper.setupRectangle();

        RenderHelper.rectangle(builder, matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for (int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder, matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, 0xFF000000 | NikesMath.hsvToRgb((hueOff + huePer * i) % 1F, 1F, 1F));
        }
        RenderHelper.rectangle(builder, matrix, mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);
        if(startText != null || endText != null) {
            if(startText != null) {
                font.drawInBatch(startText, mouseX, mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);
            }
            if (endText != null) {
                font.drawInBatch(endText, mouseX + width - font.width(endText), mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);

            }
        }
    }

    default void drawSimpleBarWithFadeAndText(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent, int startColor, int endColor, Font font, String startText, String endText) {
        int fillWidth = Math.min(width, (int) (width * percent));
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));

        BufferBuilder builder = RenderHelper.setupRectangle();

        RenderHelper.rectangle(builder, matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for(int i = 0; i < fillWidth; i++) {
            RenderHelper.rectangle(builder, matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, ColorUtil.fastFade(startColor, endColor, ((double) i) / fillWidth));
        }
        RenderHelper.rectangle(builder, matrix, mouseX + fillWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);

        RenderHelper.endRectangle(builder);
            if(startText != null) {
                font.drawInBatch(startText, mouseX, mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);
            }
            if (endText != null) {
                font.drawInBatch(endText, mouseX + width - font.width(endText), mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);

            }
    }

    default void drawRainbowBarWithText(Matrix4f matrix, MultiBufferSource.BufferSource source, int mouseX, int mouseY, int width, int height, int barSlot, float percent, Font font, String startText, String endText) {
        int rainbowWidth = Math.min(width, (int) (width * percent));
        float huePer = width == 0 ? 0F : 1F / width;
        float hueOff = (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.01F;
        mouseY -=(height * (barSlot-1)) + (1*(barSlot-1));

        BufferBuilder builder = RenderHelper.setupRectangle();

        RenderHelper.rectangle(builder,matrix, mouseX - 1, mouseY - height - 1, mouseX + width + 1, mouseY, 0xFF000000);
        for (int i = 0; i < rainbowWidth; i++) {
            RenderHelper.rectangle(builder, matrix, mouseX + i, mouseY - height, mouseX + i + 1, mouseY, 0xFF000000 | NikesMath.hsvToRgb((hueOff + huePer * i) % 1F, 1F, 1F));
        }
        RenderHelper.rectangle(builder, matrix, mouseX + rainbowWidth, mouseY - height, mouseX + width, mouseY, 0xFF555555);
        RenderHelper.endRectangle(builder);

        if(startText != null) {
            font.drawInBatch(startText, mouseX, mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);
        }
        if (endText != null) {
            font.drawInBatch(endText, mouseX + width - font.width(endText), mouseY - 12, 0xFFFFFF, true, matrix, source, Font.DisplayMode.NORMAL, 0, 15728880, false);

        }
    }

}
