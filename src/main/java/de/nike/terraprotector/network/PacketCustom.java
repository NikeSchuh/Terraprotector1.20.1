package de.nike.terraprotector.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PacketCustom {

    public static HashMap<Integer, Class<? extends ICustomPacket<?>>> packetRegistry = new HashMap<>();
    public static HashMap<Integer, BiConsumer<Object, NetworkEvent.Context>> packetHandlers = new HashMap<>();

    private final int packetId;
    private ICustomPacket<?> packet;
    private Object payload;

    public PacketCustom(int packetId, ICustomPacket<?> packet) {
        this.packetId = packetId;
        this.packet = packet;
    }

    public PacketCustom(FriendlyByteBuf buf) {
        this.packetId = buf.readVarInt();
        try {
           payload = packetRegistry.get(packetId).getConstructor(FriendlyByteBuf.class).newInstance(buf);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public Object getPayload() {
        return payload;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(packetId);
        packet.writeData(buf);
    }

    public int getPacketId() {
        return packetId;
    }

}
