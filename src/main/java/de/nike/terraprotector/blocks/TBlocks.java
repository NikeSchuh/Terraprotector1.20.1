package de.nike.terraprotector.blocks;

import de.nike.terraprotector.TerraProtector;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

public class TBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TerraProtector.MODID);

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
