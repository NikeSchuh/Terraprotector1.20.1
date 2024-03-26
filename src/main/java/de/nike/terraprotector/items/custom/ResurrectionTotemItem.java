package de.nike.terraprotector.items.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.client.ClientTickHandler;
import de.nike.terraprotector.client.render.IBarRenderer;
import de.nike.terraprotector.client.tooltip.TooltipBarRenderer;
import de.nike.terraprotector.items.TItemEventHandler;
import de.nike.terraprotector.lib.ColorUtil;
import de.nike.terraprotector.lib.NBTHelper;
import de.nike.terraprotector.lib.TextUtils;
import de.nike.terraprotector.network.PacketHandler;
import de.nike.terraprotector.network.packets.SPlayerResurrectEffect;
import de.nike.terraprotector.sounds.TSounds;
import de.nike.terraprotector.stats.RivenHost;
import de.nike.terraprotector.stats.RivenStat;
import de.nike.terraprotector.stats.RivenStats;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemHandler;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class ResurrectionTotemItem extends Item implements IBarRenderer, ICurioItem, IRivenSlotProvider {

    public static final int BAR_START = Color.RED.getRGB();
    public static final int BAR_END = new Color(155, 0, 155).getRGB();

    private static final DecimalFormat format = new DecimalFormat("#.##");

    public static final String TAG_INVULNERABLE_TIME = "invulnerableTime";
    public static final String TAG_REVIVE_COOLDOWN = "reviveCooldown";
    public static final String TAG_LEVEL = "totemLevel";
    public static final String TAG_SOULS = "collectedSouls";

    public ResurrectionTotemItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        if (RivenItem.hasModifiers(stack)) {
            HashMap<RivenStat<?>, Object> stats = RivenItem.getRivenStatsMap(stack);
            for (RivenStat<?> stat : stats.keySet()) {
                Object value = stats.get(stat);
                list.add(TextUtils.string(ChatFormatting.RED +  stat.getStatFormatter().format(value) + " " + ChatFormatting.GRAY + TextUtils.translation("terraprotector.stats." + stat.getRegistryName())));
            }

            float manaEfficiency = 1F+ (float) stats.getOrDefault(RivenStats.MANA_EFFICIENCY, 0F);
            float manaCost = 2000000F / manaEfficiency;

            list.add(TextUtils.string(""));

            int level = getTotemLevel(stack);
            float collecetedSouls = getCollectedSouls(stack);
            float soulGoal = getSoulGoal(level);

            list.add(TextUtils.string(ChatFormatting.GRAY + "Souls: " + ChatFormatting.RED + ((int) collecetedSouls) + "/" + (int) soulGoal));

            list.add(TextUtils.string(ChatFormatting.GRAY + "Cost: " + ChatFormatting.RED + TextUtils.formatE(manaCost)));
        }


    }

    @Override
    public void renderTooltipText(ItemStack stack, int tooltipX, int tooltipY, int width, int height, Font font, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        float coolDown = 0.0F;
        int currentCooldown = getTotemReviveCooldown(stack);

        if (currentCooldown > 0) {
            if (RivenItem.hasModifiers(stack)) {
                HashMap<RivenStat<?>, Object> stats = RivenItem.getRivenStatsMap(stack);
                int reviveCooldownTicks = (20*60) + (int) (20 * ((float) stats.getOrDefault(RivenStats.TOTEM_REVIVE_COOLDOWN, 0F)));
                coolDown = Math.max(0.0F, Math.min(1.0F, ((float) currentCooldown) / (float) reviveCooldownTicks));
            }
        }

        int level = getTotemLevel(stack);
        float collecetedSouls = getCollectedSouls(stack);
        float soulGoal = getSoulGoal(level);


        float progress = Math.max(0.0F, Math.min(1.0F, collecetedSouls / soulGoal));

        drawSimpleBarWithFadeAndText(matrix, source, tooltipX, tooltipY, width, height, 2, progress, BAR_START, BAR_END, font, TextUtils.translation("terraprotector.rank." + level), level < 7 ? TextUtils.translation("terraprotector.rank."+(level+1)) : null);
        drawSimpleBar(matrix, tooltipX, tooltipY, width, height, 1, coolDown, COOLDOWN_BAR);
    }

    private static final int COOLDOWN_BAR = new Color(100, 0, 100).getRGB();

    @Override
    public void renderTooltipImage(ItemStack stack, int tooltipX, int tooltipY, int width, int height, GuiGraphics graphics) {

    }

    public static float getCollectedSouls(ItemStack totemStack) {
        return NBTHelper.getFloat(totemStack, TAG_SOULS, 0F);
    }

    public static void setCollectedSouls(ItemStack totemStack, float value) {
        NBTHelper.setFloat(totemStack, TAG_SOULS, value);
    }

    public static int getTotemLevel(ItemStack totemStack) {
        return NBTHelper.getInt(totemStack, TAG_LEVEL, 1);
    }

    public static float getSoulGoal(int level) {
        return (float) (50 + (Math.pow(level * 25, 1.6F)));
    }

    public static int getInvulnerableTicks(ItemStack totemStack) {
        return NBTHelper.getInt(totemStack, TAG_INVULNERABLE_TIME, 0);
    }

    public static void setInvulnerableTicks(ItemStack stack, int amount) {
        NBTHelper.setInt(stack, TAG_INVULNERABLE_TIME, amount);
    }

    public static int getTotemReviveCooldown(ItemStack stack) {
        return NBTHelper.getInt(stack, TAG_REVIVE_COOLDOWN, 0);
    }

    public static void setTotemReviveCooldown(ItemStack stack, int amount) {
       NBTHelper.setInt(stack, TAG_REVIVE_COOLDOWN, amount);
    }

    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return true;
    }

    @Override
    public int getSlots(ItemStack stack) {
        return 30;
    }

    public static boolean tryBlockDamage(LivingDamageEvent event, ItemStack totemStack) {
        if(getInvulnerableTicks(totemStack) > 0) {
            event.setAmount(0.0F);
            return true;
        }
        return false;
    }

    public static boolean tryBlockDeath(LivingDeathEvent event, ItemStack stack) {
        if(event.isCanceled()) return true;

        int invTicks = getInvulnerableTicks(stack);
        int coolTicks = getTotemReviveCooldown(stack);

        if(coolTicks > 0) return false;


        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = player.serverLevel();
        if(!TItemEventHandler.lastDamage.containsKey(player)) {
            TerraProtector.LOGGER.info("Could not find last damage amount for " + event.getEntity().getDisplayName() + " :(");
            return true;
        }

        float damageReceived = TItemEventHandler.lastDamage.get(player);

        if(invTicks > 0 && damageReceived < 20000000F) {
            event.setCanceled(true);
            return true;
        }

        HashMap<RivenStat<?>, Object> stats = RivenItem.getRivenStatsMap(stack);
        float maximumDamageMultiplier = 1F + (float) stats.getOrDefault(RivenStats.TOTEM_MAXIMUM_DAMAGE_AMPLIFIER, 0F);
        float maximumDamage = (float) stats.getOrDefault(RivenStats.TOTEM_MAXIMUM_DAMAGE, 0) * maximumDamageMultiplier;

        if(damageReceived > maximumDamage) return false;

        float manaEfficiency = 1F+ (float) stats.getOrDefault(RivenStats.MANA_EFFICIENCY, 0F);
        float manaCost = 2000000F / manaEfficiency;


        if(ManaItemHandler.instance().requestManaExactForTool(stack, player,(int) manaCost, true)) {
            level.playSound(null, player.position().x, player.position().y, player.position().z, TSounds.RESURRECTION_TOTEM_POP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            PacketHandler.serverSendClient(new SPlayerResurrectEffect(player.getId()), player);
            event.setCanceled(true);
            player.setHealth(0.5F);
            player.animateHurt(1F);

            int invulnerableTicks = (int) (20 * ((float)stats.getOrDefault(RivenStats.TOTEM_INVULNERABLE_TIME, 0F)));
            if(invulnerableTicks > 0) setInvulnerableTicks(stack, invulnerableTicks);

            int reviveCooldownTicks =(20*60) + (int) (20 * ((float) stats.getOrDefault(RivenStats.TOTEM_REVIVE_COOLDOWN, 60F)));
            if(reviveCooldownTicks > 0) setTotemReviveCooldown(stack, reviveCooldownTicks);

            int effectTimeTicks = (int) (20 * ((float) stats.getOrDefault(RivenStats.TOTEM_EFFECT_TIME, 0F)));
            if(effectTimeTicks > 0) {
                int ampHealthBoos = (int) stats.getOrDefault(RivenStats.TOTEM_HEALTH_BOOST_AMPLIFIER, 0);
                int ampAbsorption = (int) stats.getOrDefault(RivenStats.TOTEM_ABSORPTION_AMPLIFIER, 0);
                int ampRegeneration = (int) stats.getOrDefault(RivenStats.TOTEM_REGENERATION_AMPLIFIER, 0);
                int ampResistance = Math.min((int) stats.getOrDefault(RivenStats.TOTEM_RESISTANCE_AMPLIFIER, 0), 3);

                if(ampHealthBoos > 0) player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, effectTimeTicks, ampHealthBoos));
                if(ampAbsorption  > 0) player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, effectTimeTicks, ampAbsorption));
                if(ampRegeneration > 0) player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, effectTimeTicks, ampRegeneration));
                if(ampResistance > 0) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, effectTimeTicks, ampResistance));
            }

            Entity attacker = event.getSource().getEntity();
            if (attacker != null) {
                attacker.setDeltaMovement(player.position().subtract(attacker.position()).normalize().multiply(-1.5, -1.5, -1.5));
            }
        }
        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        int invTicks = getInvulnerableTicks(stack);
        int coolTicks = getTotemReviveCooldown(stack);
        if(coolTicks > 0) setTotemReviveCooldown(stack, coolTicks-1);
        if(invTicks > 0) setInvulnerableTicks(stack, invTicks-1);

    }

    @Override
    public List<RivenHost> getHostList() {
        return Arrays.asList(RivenHost.MANA_USER,
                RivenHost.TOTEM);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if(stack.getItem() instanceof IBarRenderer) {
            return Optional.of(new TooltipBarRenderer((IBarRenderer) stack.getItem(), stack));
        } else {
            throw new IllegalArgumentException("Not here");
        }
    }


    public static class ManaItemImpl implements ManaItem {

        private ItemStack stack;

        public ManaItemImpl(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public int getMana() {
            return 0;
        }

        @Override
        public int getMaxMana() {
            return 0;
        }

        @Override
        public void addMana(int mana) {

        }

        @Override
        public boolean canReceiveManaFromPool(BlockEntity pool) {
            return false;
        }

        @Override
        public boolean canReceiveManaFromItem(ItemStack otherStack) {
            return true;
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
