package de.nike.terraprotector.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;


public class ClientPacketHandler {

    public static void handleSPlayResurrectEffect(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) return;
        Entity entity = level.getEntity(entityId);
        if(entity == null) return;
        level.addParticle(ParticleTypes.SOUL, entity.position().x, entity.position().y, entity.position().z, 100, 0.1F, 0.1F);
    }

}
