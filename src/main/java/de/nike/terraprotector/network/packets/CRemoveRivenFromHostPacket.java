package de.nike.terraprotector.network.packets;

import de.nike.terraprotector.network.ICustomPacket;
import de.nike.terraprotector.network.PacketCustom;
import de.nike.terraprotector.network.ServerPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CRemoveRivenFromHostPacket implements ICustomPacket<CRemoveRivenFromHostPacket> {


    private final int rivenHostSlot;
    private final int rivenInventorySlot;
    private final boolean isCurio;

    public CRemoveRivenFromHostPacket(int rivenHostSlot, int rivenInventorySlot, boolean isCurio) {
        this.rivenHostSlot = rivenHostSlot;
        this.rivenInventorySlot = rivenInventorySlot;
        this.isCurio = isCurio;
    }

    public CRemoveRivenFromHostPacket(FriendlyByteBuf buffer) {
        this.rivenHostSlot = buffer.readVarInt();
        this.rivenInventorySlot = buffer.readVarInt();
        this.isCurio = buffer.readBoolean();
    }

    public void writeData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(rivenHostSlot);
        buffer.writeVarInt(rivenInventorySlot);
        buffer.writeBoolean(isCurio);
    }

    @Override
    public CRemoveRivenFromHostPacket deserialize(FriendlyByteBuf buf) {
        return new CRemoveRivenFromHostPacket(buf);
    }

    @Override
    public int getUniqueId() {
        return 0x02;
    }

    public boolean isCurio() {
        return isCurio;
    }

    public int getRivenInventorySlot() {
        return rivenInventorySlot;
    }

    public int getRivenHostSlot() {
        return rivenHostSlot;
    }

}
