package de.nike.terraprotector.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientTickHandler {

    public static long ticksInGame = 0;
    public static float partialTicks;
    public static long frameNum;

    public static float time = 0F;
    private static final long startTime = System.currentTimeMillis();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent clientTickEvent) {
        ticksInGame++;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRender(TickEvent.RenderTickEvent clientTickEvent) {
        frameNum++;
        partialTicks = clientTickEvent.renderTickTime;
        time= (System.currentTimeMillis() - startTime) / 1000F;
    }



}
