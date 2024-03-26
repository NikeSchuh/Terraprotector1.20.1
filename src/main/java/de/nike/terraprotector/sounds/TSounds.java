package de.nike.terraprotector.sounds;

import de.nike.terraprotector.TerraProtector;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TSounds {

    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TerraProtector.MODID);

    public static final RegistryObject<SoundEvent> RESURRECTION_TOTEM_POP = registerSoundEvents("resurrection_totem_pop");

    private static RegistryObject<SoundEvent> registerSoundEvents(String soundName) {
       return SOUNDS.register(soundName, ()->SoundEvent.createVariableRangeEvent(new ResourceLocation(TerraProtector.MODID, soundName)));
    }

    public static void init(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

}
