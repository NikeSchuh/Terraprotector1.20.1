package de.nike.terraprotector.items;


import de.nike.terraprotector.items.custom.ResurrectionTotemItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class TItemEventHandler {

    public static HashMap<ServerPlayer, Float> lastDamage = new HashMap<>();

    @SubscribeEvent()
    public static void onDamage(LivingAttackEvent event) {
        if(event.isCanceled()) return;
        if(event.getEntity() instanceof ServerPlayer) {
            ServerPlayer playerEntity = (ServerPlayer) event.getEntity();


        }
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamaged(LivingDamageEvent event) {
        if(event.isCanceled()) return;
        if(event.getEntity() instanceof ServerPlayer) {
            ServerPlayer playerEntity = (ServerPlayer) event.getEntity();
            CuriosApi.getCuriosInventory(playerEntity).ifPresent(inv -> {
                List<SlotResult> totems = inv.findCurios(TItems.RESURRECTION_TOTEM.get());
                for(SlotResult result : totems) {
                    ItemStack totemStack = result.stack();
                    if(ResurrectionTotemItem.tryBlockDamage(event, totemStack)) break;
                }
            });
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            ServerPlayer playerEntity = (ServerPlayer) event.getEntity();
            CuriosApi.getCuriosInventory(playerEntity).ifPresent(inv -> {
                List<SlotResult> totems = inv.findCurios(TItems.RESURRECTION_TOTEM.get());
                for(SlotResult result : totems) {
                    ItemStack totemStack = result.stack();
                    if(ResurrectionTotemItem.tryBlockDeath(event, totemStack)) break;
                }
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void saveLastDamage(LivingAttackEvent event) {
        if(event.getEntity() instanceof ServerPlayer) {
            lastDamage.put((ServerPlayer) event.getEntity(), event.getAmount());
        }
    }







}
