package de.nike.terraprotector.network;

import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.network.packets.CMoveRivenIntoRivenHostPacket;
import de.nike.terraprotector.network.packets.CRemoveRivenFromHostPacket;
import de.nike.terraprotector.network.packets.SPlayerResurrectEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {

    public static final SimpleChannel INSTANCE =
            NetworkRegistry.ChannelBuilder.named(new ResourceLocation(TerraProtector.MODID, "main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions(version -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();



    public static void register() {
        INSTANCE.messageBuilder(PacketCustom.class, NetworkDirection.PLAY_TO_SERVER.ordinal())
                .encoder(PacketCustom::encode)
                .decoder(PacketCustom::new)
                .consumerMainThread(PacketHandler::handle)
                .add();

        INSTANCE.messageBuilder(PacketCustom.class, NetworkDirection.PLAY_TO_CLIENT.ordinal())
                .encoder(PacketCustom::encode)
                .decoder(PacketCustom::new)
                .consumerMainThread(PacketHandler::handle)
                .add();

        PacketCustom.packetRegistry.put(0x00, CMoveRivenIntoRivenHostPacket.class);
        PacketCustom.packetRegistry.put(0x02, CRemoveRivenFromHostPacket.class);
        PacketCustom.packetRegistry.put(0x03, SPlayerResurrectEffect.class);

        PacketCustom.packetHandlers.put(0x00, (rawPacket, context) -> {
            ServerPacketHandler.handleRivenInHostInsertion((CMoveRivenIntoRivenHostPacket) rawPacket, context.getSender());
        });

        PacketCustom.packetHandlers.put(0x02, (rawPacket, context) -> {
            ServerPacketHandler.handleRivenRemovalFromHost((CRemoveRivenFromHostPacket) rawPacket, context.getSender());
        });

        PacketCustom.packetHandlers.put(0x03, (rawPacket, context)-> {
            ClientPacketHandler.handleSPlayResurrectEffect(((SPlayerResurrectEffect)rawPacket).getEntityID());
        });
    }

    private static void handle(PacketCustom packetCustom, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        PacketCustom.packetHandlers.get(packetCustom.getPacketId()).accept(packetCustom.getPayload(), context);
        contextSupplier.get().setPacketHandled(true);
    }

    public static void clientSendServer(ICustomPacket<?> msg) {
        PacketCustom packetCustom = new PacketCustom(msg.getUniqueId(), msg);
        INSTANCE.send(PacketDistributor.SERVER.noArg(),packetCustom);
    }

    public static void serverSendClient(ICustomPacket<?> msg, ServerPlayer serverPlayer) {
        PacketCustom packetCustom = new PacketCustom(msg.getUniqueId(), msg);
        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(()->serverPlayer),packetCustom);
    }
}
