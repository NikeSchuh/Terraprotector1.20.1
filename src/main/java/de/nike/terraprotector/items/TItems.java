package de.nike.terraprotector.items;

import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import de.nike.terraprotector.items.custom.ResurrectionTotemItem;
import de.nike.terraprotector.items.custom.RivenItem;
import de.nike.terraprotector.items.custom.TerraProtectorItem;
import de.nike.terraprotector.stats.RivenHost;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Optional;

public class TItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TerraProtector.MODID);

    public static final RegistryObject<Item> RESURRECTION_CORE = ITEMS.register("resurrection_core", ()->new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<RivenItem> SWORD_RIVEN = ITEMS.register("sword_riven", ()-> new RivenItem(RivenHost.SWORD, RivenHost.COSMIC_SWORD, RivenHost.WEAPON, RivenHost.MANA_USER));
    public static final RegistryObject<RivenItem> PROTECTOR_RIVEN = ITEMS.register("protector_riven", ()-> new RivenItem(RivenHost.PROTECTOR, RivenHost.MANA_USER));
    public static final RegistryObject<RivenItem> TOTEM_RIVEN = ITEMS.register("totem_riven", ()-> new RivenItem(RivenHost.TOTEM, RivenHost.MANA_USER));

    public static final RegistryObject<RivenItem> ARMOR_RIVEN = ITEMS.register("armor_riven", ()-> new RivenItem(RivenHost.ARMOR, RivenHost.MANA_USER));

    public static final RegistryObject<ResurrectionTotemItem> RESURRECTION_TOTEM = ITEMS.register("resurrection_totem", ResurrectionTotemItem::new);
    public static final RegistryObject<TerraProtectorItem> PROTECTOR = ITEMS.register("protector", TerraProtectorItem::new);


    public static void init(IEventBus eventBus) {
       ITEMS.register(eventBus);
    }

}
