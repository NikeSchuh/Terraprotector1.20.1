package de.nike.terraprotector.client;

import de.nike.terraprotector.CommonProxy;
import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.shaders.TShaders;
import de.nike.terraprotector.client.tooltip.ToolTipRendererClient;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TerraProtector.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void construct() {
        super.construct();

        KeyHandler.init();
        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        eventBus.addListener(EventPriority.LOWEST, (RenderTooltipEvent.Color e) -> {
            if(e.getItemStack().getItem() instanceof IBarRenderer) {
                int width = 0;
                ToolTipRendererClient clientComponent = null;
                for (ClientTooltipComponent component : e.getComponents()) {
                    width = Math.max(width, component.getWidth(e.getFont()));
                    if (component instanceof ToolTipRendererClient c) {
                        clientComponent = c;
                    }
                }
                if (clientComponent != null) {
                    clientComponent.setContext(e.getX(), e.getY(), width);
                }
            }
        });
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onShadersLoad(RegisterShadersEvent event) {
        TShaders.loadShaders(event);
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(TooltipBarRenderer.class, ToolTipRendererClient::new);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyHandler.hudConfig);
    }

}
