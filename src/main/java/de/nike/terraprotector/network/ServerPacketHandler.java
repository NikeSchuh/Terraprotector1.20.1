package de.nike.terraprotector.network;

import de.nike.terraprotector.items.custom.IRivenSlotProvider;
import de.nike.terraprotector.items.custom.RivenHostDataHandler;
import de.nike.terraprotector.items.custom.RivenItem;
import de.nike.terraprotector.lib.InventoryUtil;
import de.nike.terraprotector.network.packets.CMoveRivenIntoRivenHostPacket;
import de.nike.terraprotector.network.packets.CRemoveRivenFromHostPacket;
import de.nike.terraprotector.stats.RivenStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.checkerframework.checker.units.qual.A;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicReference;

public class ServerPacketHandler {

    public static void handleRivenRemovalFromHost(CRemoveRivenFromHostPacket packet, ServerPlayer player) {
        int hostSlot = packet.getRivenHostSlot();

        Inventory playerInventory = player.getInventory();

        AtomicReference<ItemStack> host = new AtomicReference<>();
        if(packet.isCurio()) {
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
                host.set(curiosInventory.getEquippedCurios().getStackInSlot(hostSlot));
            });
        } else host.set(playerInventory.getItem(hostSlot));

        ItemStack hostStack = host.get();
        if(!(hostStack.getItem() instanceof IRivenSlotProvider slotProvider)) {
            System.err.println("Specified host for riven removal is not valid.");
            return;
        }

        int targetRivenSlot = packet.getRivenInventorySlot();
        if(targetRivenSlot < 0 || targetRivenSlot >= slotProvider.getSlots(hostStack)) {
            System.err.println("Invalid target riven slot specified " + targetRivenSlot);
            return;
        }

        ItemStackHandler rivenInventory = InventoryUtil.createVirtualInventory(slotProvider.getSlots(hostStack), RivenHostDataHandler.TAG_EQUIPPED_RIVEN, hostStack);
        ItemStack currentItemInSlot = rivenInventory.getStackInSlot(targetRivenSlot);

        if(currentItemInSlot.equals(ItemStack.EMPTY)) {
            System.err.println("Riven slot is empty? " + targetRivenSlot);
            return;
        }

        ServerLevel level = player.serverLevel();
        level.addFreshEntity(new ItemEntity(level, player.position().x, player.position().y, player.position().z, currentItemInSlot));

        rivenInventory.setStackInSlot(targetRivenSlot, ItemStack.EMPTY);
        RivenHostDataHandler.updateRivenStatsData(rivenInventory, hostStack);
        InventoryUtil.serializeInventory(rivenInventory, RivenHostDataHandler.TAG_EQUIPPED_RIVEN, hostStack);
    }

    public static void handleRivenInHostInsertion(CMoveRivenIntoRivenHostPacket packet, ServerPlayer player) {

        Inventory playerInventory = player.getInventory();
        int rivenSlot = packet.getRivenInventorySlot();
        int hostSlot = packet.getDestinationHost();
        if(rivenSlot == -1 || rivenSlot+1>playerInventory.getContainerSize()) {
            System.err.println("Riven Slot invalid!");
            return;
        }
        ItemStack rivenStack = playerInventory.getItem(rivenSlot);
        if(!(rivenStack.getItem() instanceof RivenItem)) {
            System.err.println("Riven Slot Item is not a Riven Item!");
            return;
        }

        AtomicReference<ItemStack> host = new AtomicReference<>(null);

        if(packet.isDestinationCurio()) {
            CuriosApi.getCuriosInventory(player).ifPresent(curiosInventory -> {
              host.set(curiosInventory.getEquippedCurios().getStackInSlot(hostSlot));
            });
        } else host.set(playerInventory.getItem(hostSlot));

        if(host.get() == null) {
            System.err.println("Could not find host item!!");
            return;
        }

        ItemStack hostStack = host.get();
        IRivenSlotProvider slotProvider = (IRivenSlotProvider) hostStack.getItem();
        if(!(hostStack.getItem() instanceof IRivenSlotProvider)) {
            System.err.println("Specified item is not a riven host!");
            return;
        }

        int targetRivenSlot = packet.getTargetRivenSlot();
        if(targetRivenSlot < 0 || targetRivenSlot >= slotProvider.getSlots(hostStack)) {
            System.err.println("Invalid target riven slot specified " + targetRivenSlot);
            return;
        }



        ItemStackHandler rivenInventory = InventoryUtil.createVirtualInventory(slotProvider.getSlots(hostStack), RivenHostDataHandler.TAG_EQUIPPED_RIVEN, hostStack);
        ItemStack currentItemInSlot = rivenInventory.getStackInSlot(targetRivenSlot);
        if(!(currentItemInSlot.equals(ItemStack.EMPTY))) {
            System.err.println("Riven slot already filled!");
            return;
        }

        rivenInventory.setStackInSlot(targetRivenSlot, rivenStack.copy());
        playerInventory.setItem(rivenSlot, ItemStack.EMPTY);
        RivenHostDataHandler.updateRivenStatsData(rivenInventory, hostStack);
        InventoryUtil.serializeInventory(rivenInventory, RivenHostDataHandler.TAG_EQUIPPED_RIVEN, hostStack);
    }

}
