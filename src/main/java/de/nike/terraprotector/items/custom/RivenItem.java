package de.nike.terraprotector.items.custom;

import com.mojang.datafixers.util.Pair;
import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import de.nike.terraprotector.lib.TextUtils;
import de.nike.terraprotector.stats.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.api.mana.ManaItem;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class RivenItem extends Item implements IBarRenderer {

    public static final String TAG_MODIFIERS = "combinedStats";
    public static final String TAG_LEVEL = "rivenLevel";
    public static final String TAG_MULTIPLIER = "rivenMult";
    public static final String TAG_MANA = "storedMana";
    public static final String TAG_VALUE = "rivenValue";

    private final List<RivenHost> hosts;
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public static final Random rivenRandom = new Random();

    public RivenItem(RivenHost... hosts) {
        super(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1));
        this.hosts = Arrays.asList(hosts);
    }

    public List<RivenHost> getHosts() {
        return hosts;
    }

    public static List<Pair<RivenStat<?>, Object>> getRivenStats(ItemStack stack) {
        List<Pair<RivenStat<?>, Object>> stats = new ArrayList<>();
        CompoundTag modifiers = stack.getOrCreateTag().getCompound(TAG_MODIFIERS);
        for(String registryName : modifiers.getAllKeys()) {
            Optional<RivenStat<?>> optional = RivenStats.getRivenStat(registryName);
            optional.ifPresent(stat -> {
                DataType type = stat.getDataType();
                var val = type.deserialize(modifiers, registryName);
                var value = type.multiply(val, getRivenStatMult(stack));
                stats.add(new Pair<>(stat, value));
            });
        }
        return stats;
    }



    public static HashMap<RivenStat<?>, Object> getRivenStatsMap(ItemStack stack) {
        if(RivenStats.cache.containsKey(stack)) return RivenStats.cache.get(stack);
        HashMap<RivenStat<?>, Object> stats = new HashMap<>();
        CompoundTag modifiers = stack.getOrCreateTag().getCompound(TAG_MODIFIERS);
        for(String registryName : modifiers.getAllKeys()) {
            Optional<RivenStat<?>> optional = RivenStats.getRivenStat(registryName);
            optional.ifPresent(stat -> {
                DataType type = stat.getDataType();
                var val = type.deserialize(modifiers, registryName);
                var value = type.multiply(val, getRivenStatMult(stack));
                stats.put(stat, value);
            });
        }
        RivenStats.cache.put(stack, stats);
        return stats;
    }




    public static void setRivenValue(ItemStack stack, float rivenValue) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putFloat(TAG_VALUE, rivenValue);
    }

    public static float getRivenValue(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.contains(TAG_VALUE) ? nbt.getFloat(TAG_VALUE) : -1F;
    }

    public static boolean hasRivenValue(ItemStack stack) {
        return stack.getOrCreateTag().contains(TAG_VALUE);
    }

    public static float getPercentProgress(ItemStack stack) {
        int mana = getRivenMana(stack);
        int needed = getRivenLevelCost(stack);
        return Math.max(0.0F, Math.min(1.0F, ((float) mana) / needed));
    }

    public static float getRivenStatMult(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.contains(TAG_MULTIPLIER) ? nbt.getFloat(TAG_MULTIPLIER) : 1.0F;
    }

    public static void setRivenStatMult(ItemStack stack, float mult) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putFloat(TAG_MULTIPLIER, mult);
    }

    public static int getRivenMana(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return hasRivenMana(stack) ? nbt.getInt(TAG_MANA) : 0;
    }

    public static boolean hasRivenMana(ItemStack stack) {
        return stack.getOrCreateTag().contains(TAG_MANA);
    }
    public static int getRivenLevel(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return hasRivenLevel(stack) ? nbt.getInt(TAG_LEVEL) : 0;
    }

    public static void addRivenLevel(ItemStack riven) {
        CompoundTag nbt = riven.getOrCreateTag();
        nbt.putInt(TAG_LEVEL, getRivenLevel(riven) + 1);
    }

    public static int getRivenLevelCost(ItemStack riven) {
        int level = getRivenLevel(riven);
        int baseCost = 250000;
        float rivenValue = getRivenValue(riven);
        return (int) ((baseCost * Math.pow(level + 1F, 2.0F)) * rivenValue);
    }

    public static void addRivenMana(ItemStack riven, int amount) {
        int mana = getRivenMana(riven) + amount;
        setRivenMana(riven, mana);
        int cost = getRivenLevelCost(riven);
        if(mana >= cost) {
            addRivenLevel(riven);
            setRivenMana(riven, getRivenMana(riven) - cost);
            setRivenStatMult(riven, getRivenStatMult(riven) * 1.1F);
        }
    }

    public static void setRivenMana(ItemStack riven, int value) {
        riven.getOrCreateTag().putInt(TAG_MANA, value);
    }



    public static boolean hasModifiers(ItemStack riven) {
        return riven.getOrCreateTag().contains(TAG_MODIFIERS);
    }

    public static boolean isCompatibleWith(IRivenSlotProvider provider, RivenItem rivenItem) {
        for(RivenHost host : rivenItem.hosts) {
            if(!provider.getHostList().contains(host)) return false;
        }
        return true;
    }

    public static boolean hasRivenLevel(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        return nbt.contains(TAG_LEVEL);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int tick, boolean b) {
        if(EffectiveSide.get().isServer() && !hasModifiers(stack) && entity.tickCount % 20 == 0) {
            RivenStats.generateRiven(stack, hosts);
        }
    }

    public static List<Component> getLore(ItemStack stack) {
        List<Component> lore = new ArrayList<>();
        CompoundTag modifiers = stack.getOrCreateTag().getCompound(TAG_MODIFIERS);
        for(String registryName : modifiers.getAllKeys()) {
            Optional<RivenStat<?>> optional = RivenStats.getRivenStat(registryName);
            optional.ifPresent(stat -> {
                DataType type = stat.getDataType();
                StatFormatter formatter = stat.getStatFormatter();
                var cv = type.deserialize(modifiers, registryName);
                if(type.isOperateable()) cv = type.multiply(cv, getRivenStatMult(stack));
                lore.add(TextUtils.string(ChatFormatting.GRAY + formatter.format(cv) + " " + TextUtils.translation("terraprotector.stats." + registryName)));
            });


        }
        return lore;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> lore, TooltipFlag flags) {

        if(hasModifiers(stack)) {
            CompoundTag modifiers = stack.getOrCreateTag().getCompound(TAG_MODIFIERS);
            for(String registryName : modifiers.getAllKeys()) {
                Optional<RivenStat<?>> optional = RivenStats.getRivenStat(registryName);
                optional.ifPresent(stat -> {
                    DataType type = stat.getDataType();
                    StatFormatter formatter = stat.getStatFormatter();
                    var cv = type.deserialize(modifiers, registryName);
                    if(type.isOperateable()) cv = type.multiply(cv, getRivenStatMult(stack));
                    lore.add(TextUtils.string(ChatFormatting.GRAY + formatter.format(cv) + " " + TextUtils.translation("terraprotector.stats." + registryName)));
                });


            }
        } else {
            lore.add(TextUtils.string(ChatFormatting.GRAY + "Riven has no stats yet."));
            lore.add(TextUtils.string(ChatFormatting.GRAY + "Generates stat when picked up"));

        }
        lore.add(TextUtils.string(" "));
        if(hasRivenValue(stack)) {
            int level = getRivenLevel(stack);
            if(level < 6) {
                lore.add(TextUtils.string(ChatFormatting.GREEN + decimalFormat.format(((float) getRivenMana(stack)) / 1000000) + ChatFormatting.GRAY + " / " + ChatFormatting.GREEN + decimalFormat.format(((float) getRivenLevelCost(stack)) / 1000000)));
            }
        }
        super.appendHoverText(stack, world, lore, flags);
    }

    @Override
    public void renderTooltipImage(ItemStack stack, int tooltipX, int tooltipY, int width, int height, GuiGraphics graphics) {

    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if(stack.getItem() instanceof IBarRenderer) {
            return Optional.of(new TooltipBarRenderer((IBarRenderer) stack.getItem(), stack));
        } else {
            throw new IllegalArgumentException("Not here");
        }

    }

    @Override
    public void renderTooltipText(ItemStack stack, int tooltipX, int tooltipY, int width, int height, Font font, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        int level = getRivenLevel(stack) + 1;
        if(level >=7) {
            drawSimpleBarWithRainbowWaveAndText(matrix, source, tooltipX, tooltipY, width, height, 1, 1.0F, font, TextUtils.translation("terraprotector.rank." + 7), "");
        } else {
            drawSimpleBarWithRainbowWaveAndText(matrix, source, tooltipX, tooltipY, width, height, 1, getPercentProgress(stack), font, TextUtils.translation("terraprotector.rank." + level), TextUtils.translation("terraprotector.rank." + (level + 1)));
        }
    }

    public static class ManaItemImpl implements ManaItem {

        private ItemStack stack;

        public ManaItemImpl(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getMana() {
            return RivenItem.getRivenMana(stack);
        }

        @Override
        public int getMaxMana() {
            return RivenItem.getRivenLevelCost(stack);
        }

        @Override
        public void addMana(int mana) {
            RivenItem.addRivenMana(stack, mana);
        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool) {
            return RivenItem.hasRivenValue(stack);
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack otherStack) {
            return false;
        }

        @Override
        public boolean canExportManaToPool(BlockEntity pool) {
            return false;
        }

        @Override
        public boolean canExportManaToItem(ItemStack otherStack) {
            return false;
        }

        @Override
        public boolean isNoExport() {
            return true;
        }
    }
}
