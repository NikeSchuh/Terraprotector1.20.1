package de.nike.terraprotector.client.render;

import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public interface INikeRenderer {

    void beginTexture(Matrix4f matrix4f);
    void endTexture();
    void renderSprite(ResourceLocation sprite, float x, float y, float width, float height);
    void beginShape(ShapeType type, Matrix4f matrix4f);
    void endShape();
    void beginText();
    default void renderText(String text, Matrix4f matrix, float x, float y) {
        renderText(text, matrix, x, y, 0xFFFFFF, true);
    }
    void renderText(String text, Matrix4f matrix, float x, float y, int color, boolean shadow);
    void endText();
    void resetDrawCalls();
    int getDrawCalls();

    void line(float x1, float y1, float x2, float y2, int colorStart, int colorEnd);
    void rect(float x, float y, float width, float height, int color);

    default void line(float x1, float y1, float x2, float y2, int color) {
        line(x1, y1, x2, y2, color, color);
    }


}
