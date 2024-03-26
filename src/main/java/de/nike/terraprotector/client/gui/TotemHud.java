package de.nike.terraprotector.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nike.terraprotector.client.render.INikeRenderer;
import de.nike.terraprotector.client.render.NikeForgeRenderer;
import de.nike.terraprotector.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;

public class TotemHud extends AbstractHudElement{

    private static boolean render;
    private static Minecraft minecraft;

    private static int souls;
    private static int soulGoal;
    private static int level;
    private static int displayTicks;

    INikeRenderer renderer;

    public TotemHud(Vector2f defaultRawPos, String name) {
        super(defaultRawPos, name);
        renderer = new NikeForgeRenderer();
        minecraft = Minecraft.getInstance();
        width = 25;
        height = 2;
    }

    @Override
    public void tick(boolean configuring) {

    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks, boolean configuring) {
        RenderSystem.enableBlend();

        RenderSystem.disableBlend();
    }
}
