package de.nike.terraprotector.events;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.render.INikeRenderer;
import de.nike.terraprotector.client.render.NikeForgeRenderer;
import de.nike.terraprotector.client.render.RenderHelper;
import de.nike.terraprotector.client.render.ShapeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;

import java.awt.*;

@Mod.EventBusSubscriber(modid = TerraProtector.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    private static INikeRenderer renderer = new NikeForgeRenderer();

    @SubscribeEvent
    public static void gatherToolTipComponents(RenderTooltipEvent.GatherComponents event) {

    }

    static long ticks = 0;

    @SubscribeEvent
    public static void renderHud(RenderGuiOverlayEvent.Pre event) {
        if (event.isCanceled()) return;
        ticks++;

    }

}
