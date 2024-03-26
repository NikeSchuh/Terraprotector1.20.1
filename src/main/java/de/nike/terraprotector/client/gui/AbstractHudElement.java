package de.nike.terraprotector.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHudElement  {

    public Vector2f rawPos;
    protected int width = 100;
    protected int height = 20;
    protected boolean enabled = true;
    protected double dragXOffset = 0;
    protected double dragYOffset = 0;
    private Runnable changeListener = null;
    private String name;

    public AbstractHudElement(Vector2f defaultRawPos, String name) {
        this.rawPos = defaultRawPos;
        this.name = name;

    }

    public int width() {
        return width;
    }
    public int height() {
        return height;
    }
    public abstract void tick(boolean configuring);
    public abstract void render(GuiGraphics graphics, float partialTicks, boolean configuring);

    public String getName() {
        return name;
    }

    public boolean shouldRender(RenderGuiOverlayEvent.Pre type, boolean preRenderEvent) {
        return true;
    }

    public int xPos() {
        int screen = screenWidth();
        double pos = screen * rawPos.x;
        pos -= width() * rawPos.x;
        return (int) pos;
    }

    /**
     * @return This is the Y position for the top left corner of this hud element.
     */
    public int yPos() {
        int screen = screenHeight();
        double pos = screen * rawPos.y;
        pos -= height() * rawPos.y;
        return (int) pos;
    }

    public int screenWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public int screenHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public int getConfigWidth() {
        return width();
    }

    public int getConfigHeight() {
        return height;
    }

    public int getConfigOffsetX() {
        return 0;
    }

    public int getConfigOffsetY() {
        return 0;
    }

    public Component getInfoTooltip() {
        return Component.literal("Emppty");
    }

    public void saveToConfig() {

    }
}