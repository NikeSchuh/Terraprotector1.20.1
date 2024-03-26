package de.nike.terraprotector.network.packets;

import de.nike.terraprotector.network.ICustomPacket;
import net.minecraft.network.FriendlyByteBuf;

public class SPlayerResurrectEffect implements ICustomPacket<SPlayerResurrectEffect> {

    private final int entityID;

    public SPlayerResurrectEffect(int entityID) {
        this.entityID = entityID;
    }

    public SPlayerResurrectEffect(FriendlyByteBuf buf) {
        this.entityID = buf.readVarInt();
    }


    @Override
    public void writeData(FriendlyByteBuf buf) {
        buf.writeVarInt(entityID);
    }

    @Override
    public SPlayerResurrectEffect deserialize(FriendlyByteBuf buf) {
        return new SPlayerResurrectEffect(buf);
    }

    public int getEntityID() {
        return entityID;
    }

    @Override
    public int getUniqueId() {
        return 0x03;
    }
}
