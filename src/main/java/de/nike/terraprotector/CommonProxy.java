package de.nike.terraprotector;

import com.google.common.base.Suppliers;
import de.nike.terraprotector.blocks.TBlocks;
import de.nike.terraprotector.config.Config;
import de.nike.terraprotector.items.TItems;
import de.nike.terraprotector.items.custom.ResurrectionTotemItem;
import de.nike.terraprotector.items.custom.RivenItem;
import de.nike.terraprotector.network.PacketHandler;
import de.nike.terraprotector.sounds.TSounds;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.forge.CapabilityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class CommonProxy {

    private static List<Consumer<MinecraftServer>> startHooks = new ArrayList<>();

    public void construct() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        TBlocks.init(modEventBus);
        TItems.init(modEventBus);
        TSounds.init(modEventBus);

        MinecraftForge.EVENT_BUS.addListener(this::serverStart);
        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::attachCapabilities);
    }

    public void clientSetup(FMLClientSetupEvent event) {

    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }

    public void serverSetup(FMLDedicatedServerSetupEvent event) {

    }

    public void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        var makeManaItem = MANA_ITEM.get().get(stack.getItem());
        if (makeManaItem != null) {
            event.addCapability(prefix("mana_item"),
                    CapabilityUtil.makeProvider(BotaniaForgeCapabilities.MANA_ITEM, makeManaItem.apply(stack)));
        }
    }

    private static final Supplier<Map<Item, Function<ItemStack, ManaItem>>> MANA_ITEM = Suppliers.memoize(() -> Map.of(
            TItems.SWORD_RIVEN.get(), RivenItem.ManaItemImpl::new,
            TItems.RESURRECTION_TOTEM.get(), ResurrectionTotemItem.ManaItemImpl::new
    ));

    //  TItems.REMOTE_MANA_POOL.get(), RemoteManaPoolItem.CoordBoundItemImpl::new
    private static final Supplier<Map<Item, Function<ItemStack, CoordBoundItem>>> COORD_ITEM = Suppliers.memoize(Map::of);

    public void serverStart(ServerStartedEvent event) {
        startHooks.forEach(hook -> hook.accept(event.getServer()));
    }

    public boolean addServerStartHook(Consumer<MinecraftServer> consumer) {
        return startHooks.add(consumer);
    }



}
