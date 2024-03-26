package de.nike.terraprotector.network;

import net.minecraft.network.FriendlyByteBuf;

public interface ICustomPacket<S> {

    void writeData(FriendlyByteBuf buf);
    S deserialize(FriendlyByteBuf buf);
    int getUniqueId();

}
