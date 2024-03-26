package de.nike.terraprotector.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class NikeForgeRenderer implements INikeRenderer{

    BufferBuilder buffer;
    boolean textureBuffer = false;
    boolean shapeBuffer = false;
    ShapeType type = ShapeType.FILLED;
    Matrix4f matrix4f;
    ResourceLocation currentTexture;

    int drawCalls = 0;


    public NikeForgeRenderer() {

    }

    @Override
    public int getDrawCalls() {
        return drawCalls;
    }

    @Override
    public void resetDrawCalls() {
        drawCalls = 0;
    }

    @Override
    public void beginTexture(Matrix4f matrix4f) {
        if(shapeBuffer) throw new IllegalArgumentException("Already called beginShape");
        textureBuffer = true;
        this.matrix4f = matrix4f;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    }

    @Override
    public void endTexture() {
        checkForTextureRender();
        BufferUploader.drawWithShader(buffer.end());
        drawCalls++;
    }

    private void checkForShapeRender() {
        if(textureBuffer) throw new IllegalStateException("Call endTextures() and beginShape() before!");
        if(!shapeBuffer) throw new IllegalStateException("Call beginShape() before!");
    }

    private void checkForTextureRender() {
        if(shapeBuffer) throw new IllegalStateException("Call endShape() and beginTexture() before!");
        if(!textureBuffer) throw new IllegalStateException("Call beginTexture() before!");
    }

    @Override
    public void renderSprite(ResourceLocation sprite, float x, float y, float width, float height) {
        checkForTextureRender();
        if(sprite != currentTexture) {
            flushTexture();
        }

        float u0 = 0.0f;
        float v0 = 0.0f;
        float u1 = 1.0f;
        float v1 = 1.0f;

        float x1 = x;
        float y1 = y;
        float x2 = x + width;
        float y2 = y + height;

        buffer.vertex(x1, y1, 0.0F).uv(u0, v0).endVertex();
        buffer.vertex(x1, y2, 0.0F).uv(u0, v1).endVertex();
        buffer.vertex(x2, y2, 0.0F).uv(u1, v1).endVertex();
        buffer.vertex(x2, y1, 0.0F).uv(u1, v0).endVertex();
    }

    @Override
    public void beginShape(ShapeType type, Matrix4f matrix4f) {
        if(textureBuffer) throw new IllegalArgumentException("Already called beginShape");
        shapeBuffer = true;
        this.type = type;
        this.matrix4f = matrix4f;
        buffer = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(type == ShapeType.FILLED ? VertexFormat.Mode.QUADS : VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR);
    }

    @Override
    public void endShape() {
        checkForShapeRender();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
        shapeBuffer = false;
        buffer = null;
    }

    private MultiBufferSource.BufferSource bufferSource;

    @Override
    public void beginText() {
        bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
    }

    @Override
    public void renderText(String text, Matrix4f matrix, float x, float y, int color, boolean shadow) {
        Font font = Minecraft.getInstance().font;
        font.drawInBatch(text,x, y, color, shadow, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, false);
    }

    @Override
    public void endText() {
        bufferSource.endBatch();
    }

    @Override
    public void line(float x1, float y1, float x2, float y2, int colorStart, int colorEnd) {
        checkForShapeRender();
    }

    @Override
    public void rect(float x, float y, float width, float height, int color) {
        checkForShapeRender();
        if(type == ShapeType.FILLED) {
            float x1 = x;
            float x2 = (x + width);
            float y1 = (int) y;
            float y2 = (int) (y + height);

            if (x1 < x2) {
                float i = x1;
                x1 = x2;
                x2 = i;
            }
            if (y1 < y2) {
                float j = y1;
                y1 = y2;
                y2 = j;
            }
            float f3 = (float)(color >> 24 & 255) / 255.0F;
            float f = (float)(color >> 16 & 255) / 255.0F;
            float f1 = (float)(color >> 8 & 255) / 255.0F;
            float f2 = (float)(color & 255) / 255.0F;

            buffer.vertex(matrix4f, x1, y2, 0.0F).color(f, f1, f2, f3).endVertex();
            buffer.vertex(matrix4f, x2, y2, 0.0F).color(f, f1, f2, f3).endVertex();
            buffer.vertex(matrix4f, x2, y1, 0.0F).color(f, f1, f2, f3).endVertex();
            buffer.vertex(matrix4f, x1, y1, 0.0F).color(f, f1, f2, f3).endVertex();

        }
    }


    private void flushTexture() {
        checkForTextureRender();
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawCalls++;
    }
}
