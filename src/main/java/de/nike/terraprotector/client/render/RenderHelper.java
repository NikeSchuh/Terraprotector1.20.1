package de.nike.terraprotector.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderHelper {

    public static void renderSprite(PoseStack matrixStack, ResourceLocation sprite, int posX, int posY, int width, int height) {
        RenderSystem.setShaderTexture(0, sprite);
     //   GuiComponent.blit(matrixStack, posX, posY, 10, 0F, 0F, width, height, width, height);
    }

    public static MultiBufferSource.BufferSource setupText() {
        return MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
    }

    public static void endText(MultiBufferSource.BufferSource source) {
        source.endBatch();
    }

    public static BufferBuilder setupRectangle() {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
     //   RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        return bufferbuilder;
    }

    public static BufferBuilder setupRectangle(ShaderInstance shaderInstance) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        //   RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(()-> shaderInstance);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        return bufferbuilder;
    }

    public static BufferBuilder setupCustomRectangle() {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        //   RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        return bufferbuilder;
    }

    public static void rectangle(BufferBuilder builder, Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            int j = y1;
            y1 = y2;
            y2 = j;
        }
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        builder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(f, f1, f2, f3).endVertex();
        builder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(f, f1, f2, f3).endVertex();
        builder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(f, f1, f2, f3).endVertex();
        builder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(f, f1, f2, f3).endVertex();

    }

    public static void endRectangle(BufferBuilder builder) {
        BufferUploader.drawWithShader(builder.end());
       // RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void rectangleSized(BufferBuilder builder, Matrix4f matrix, int x, int y, int width, int height, int color) {
        rectangle(builder, matrix, x, y, x + width, y + height, color);
    }

    public static BufferBuilder setupTexture() {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
      //  RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        return bufferbuilder;
    }



    public static void endTexture(BufferBuilder builder) {
        BufferUploader.drawWithShader(builder.end());
    }

}