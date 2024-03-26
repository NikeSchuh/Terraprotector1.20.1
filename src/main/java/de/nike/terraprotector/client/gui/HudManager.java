package de.nike.terraprotector.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class HudManager {
    private static HashMap<String, AbstractHudElement> elementList= new HashMap<>();

    @SubscribeEvent
    public static void onDrawOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.isCanceled()) return;
        boolean configuring = false;
        for (AbstractHudElement element : elementList.values()) {
            element.render(event.getGuiGraphics(), event.getPartialTick(), configuring);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        boolean configuring = false;
        for (AbstractHudElement element : elementList.values()) {
            element.tick(configuring);
        }
    }

    public static void registerElement(String name, AbstractHudElement element) {
        elementList.put(name, element);
    }
    public static Collection<AbstractHudElement> getRegisteredElements() {
        return elementList.values();
    }
    public static AbstractHudElement getHud(String name) {
        return elementList.get(name);
    }


}
