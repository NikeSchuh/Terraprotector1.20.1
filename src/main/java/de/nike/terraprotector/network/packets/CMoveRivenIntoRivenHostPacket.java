package de.nike.terraprotector.network.packets;

import de.nike.terraprotector.network.ICustomPacket;
import de.nike.terraprotector.network.PacketCustom;
import de.nike.terraprotector.network.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CMoveRivenIntoRivenHostPacket implements ICustomPacket<CMoveRivenIntoRivenHostPacket> {

    private final int rivenInventorySlot;
    private final int destinationHost;
    private final boolean isDestinationCurio;
    private final int targetRivenSlot;


    public CMoveRivenIntoRivenHostPacket(int rivenInventorySlot, int destinationHost, boolean isDestinationCurio, int targetRivenSlot) {
        this.rivenInventorySlot = rivenInventorySlot;
        this.destinationHost = destinationHost;
        this.isDestinationCurio = isDestinationCurio;
        this.targetRivenSlot = targetRivenSlot;
    }

    public CMoveRivenIntoRivenHostPacket(FriendlyByteBuf buffer) {
        this.rivenInventorySlot = buffer.readVarInt();
        this.destinationHost = buffer.readVarInt();
        this.isDestinationCurio = buffer.readBoolean();
        this.targetRivenSlot = buffer.readVarInt();
    }

    public void writeData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(rivenInventorySlot);
        buffer.writeVarInt(destinationHost);
        buffer.writeBoolean(isDestinationCurio);
        buffer.writeVarInt(targetRivenSlot);
    }

    @Override
    public CMoveRivenIntoRivenHostPacket deserialize(FriendlyByteBuf buf) {
        return new CMoveRivenIntoRivenHostPacket(buf);
    }

    @Override
    public int getUniqueId() {
        return 0x00;
    }

    public int getTargetRivenSlot() {
        return targetRivenSlot;
    }

    public int getRivenInventorySlot() {
        return rivenInventorySlot;
    }

    public int getDestinationHost() {
        return destinationHost;
    }

    public boolean isDestinationCurio() {
        return isDestinationCurio;
    }

}
