package de.nike.terraprotector.client;

import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.screens.RivenScreen;
import de.nike.terraprotector.items.custom.RivenItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = TerraProtector.MODID)
public class KeyEventHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        handleInput(player);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        handleInput(player);
    }

    private static void handleInput(Player player) {
        if(KeyHandler.hudConfig.consumeClick()) {
            if(RivenItemHandler.hasRivenHostProviders(player)) {
                Minecraft.getInstance().setScreen(new RivenScreen());
            } else player.sendSystemMessage(Component.translatable("terraprotector.messages.no_hosts_equiped"));

        }
    }


}
