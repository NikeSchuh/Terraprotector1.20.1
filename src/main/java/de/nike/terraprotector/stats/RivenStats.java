package de.nike.terraprotector.stats;

import de.nike.terraprotector.TerraProtector;
import de.nike.terraprotector.items.custom.RivenItem;
import de.nike.terraprotector.lib.NikesMath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

import static de.nike.terraprotector.items.custom.RivenItem.TAG_MODIFIERS;
import static de.nike.terraprotector.items.custom.RivenItem.hasModifiers;

public class RivenStats {

    private static HashMap<String, RivenStat<?>> statRegistry = new HashMap<>();
    public static final HashMap<ItemStack, HashMap<RivenStat<?>, Object>> cache = new HashMap<>();
    public static final String TAG_STATS = "rivenStats";

    public static boolean isRegistered(String registryName) {
        return statRegistry.containsKey(registryName);
    }

    public static Optional<RivenStat<?>> getRivenStat(String registryName) {
        return Optional.of(statRegistry.getOrDefault(registryName, null));
    }

    protected static void register(RivenStat<?> rivenStat) {
        if(RivenStats.isRegistered(rivenStat.getRegistryName())) {
            throw new IllegalArgumentException("Already registered! " + rivenStat.getRegistryName());
        } else statRegistry.put(rivenStat.getRegistryName(), rivenStat);
        TerraProtector.LOGGER.info("\"terraprotector.stats." + rivenStat.getRegistryName() + "\":\"\",");
    }

    // General
    public static RivenStat<Float> MANA_EFFICIENCY = new RivenStat<>("manaEfficiency", DataType.FLOAT, true, true, 2.75F, 0.01F, RollManager.exponentialFloat(0.01F, 1.5F, 4.0F), StatFormatter.standardPercentage, RivenHost.MANA_USER);

    // Cosmic Sword Special
    public static RivenStat<Integer> COSMIC_HIT_AMOUNT = new RivenStat<>("cataclysmaCosmicHits", DataType.INT, true, true, 6.5F, 0.055F, RollManager.linearInt(1, 5), StatFormatter.standardInt, RivenHost.COSMIC_SWORD);
    public static RivenStat<Float> COSMIC_HIT_DAMAGE = new RivenStat<>("cataclysmaCosmicHitsDamage", DataType.FLOAT, true, true, 3.5F, 0.05F, RollManager.exponentialFloat(0.01F, 10F, 5.0F), StatFormatter.standardFloat, RivenHost.COSMIC_SWORD);
    public static RivenStat<Float> COSMIC_HIT_DAMAGE_MULTIPLIER = new RivenStat<>("cataclysmaCosmicHitsDamageMultiplier", DataType.FLOAT, true, true, 4.75F, 0.04F, RollManager.exponentialFloat(0.01F, 0.5F, 5.0F), StatFormatter.standardPercentage, RivenHost.COSMIC_SWORD);
    public static RivenStat<Float> COSMIC_HIT_SPEED = new RivenStat<>("cataclysmaCosmicHitsSpeed", DataType.FLOAT, true, true, 6.75F, 0.08F, RollManager.exponentialFloat(0.01F, 1.0F, 5.0F), StatFormatter.standardPercentage, RivenHost.COSMIC_SWORD);
    public static RivenStat<Float> COSMIC_HIT_CRIT_CHANCE = new RivenStat<>("cataclysmaCosmicHitsCritChance", DataType.FLOAT, true, true, 6.5F, 0.02F, RollManager.exponentialFloat(0.01F, 0.25F, 4.0F), StatFormatter.standardPercentage, RivenHost.COSMIC_SWORD);
    public static RivenStat<Float> COSMIC_HIT_CRIT_DAMAGE = new RivenStat<>("cataclysmaCosmicHitsCritDamage", DataType.FLOAT, true, true, 8.5F, 0.015F, RollManager.exponentialFloat(0.01F, 0.25F, 5.25F), StatFormatter.standardPercentage, RivenHost.COSMIC_SWORD);

    // Weapons in General

    public static RivenStat<Float> ATTACK_DAMAGE = new RivenStat<>("attackDamage", DataType.FLOAT, true, true, 2.5F, 0.09F, RollManager.exponentialFloat(0.01F, 30F, 5.0F), StatFormatter.standardFloat, RivenHost.WEAPON);
    public static RivenStat<Float> ATTACK_DAMAGE_MULTIPLIER = new RivenStat<>("attackDamageMultiplier", DataType.FLOAT, true, true, 5.75F, 0.035F, RollManager.exponentialFloat(0.01F, 1.25F, 5.0F), StatFormatter.standardPercentage, RivenHost.WEAPON);
    public static RivenStat<Float> CRIT_CHANCE = new RivenStat<>("critChance", DataType.FLOAT, true, true, 6.75F, 0.025F, RollManager.exponentialFloat(0.01F, 0.25F, 5.0F), StatFormatter.standardPercentage, RivenHost.WEAPON);
    public static RivenStat<Float> CRIT_DAMAGE_MULTIPLIER = new RivenStat<>("critDamage", DataType.FLOAT, true, true, 6.75F, 0.025F, RollManager.exponentialFloat(0.01F, 1.75F, 5.0F), StatFormatter.standardPercentage, RivenHost.WEAPON);
    public static RivenStat<Float> MAXIMUM_HEALTH_DAMAGE = new RivenStat<>("maximumHealthDamage", DataType.FLOAT, true, true, 7.75F, 0.0175F, RollManager.exponentialFloat(0.01F, 0.0875F, 5.0F), StatFormatter.standardPercentage, RivenHost.WEAPON);
    public static RivenStat<Float> EXECUTION_PERCENTAGE = new RivenStat<>("executionPercentage", DataType.FLOAT, true, true, 7.75F, 0.0175F, RollManager.exponentialFloat(0.01F, 0.0875F, 5.0F), StatFormatter.standardPercentage, RivenHost.WEAPON);
    public static RivenStat<Float> TRUE_DAMAGE = new RivenStat<>("trueDamage", DataType.FLOAT, true, true, 12.5F, 0.008F, RollManager.exponentialFloat(0.01F, 5F, 5.25F), StatFormatter.standardFloat, RivenHost.WEAPON);

    // Totem
    public static RivenStat<Float> TOTEM_DROPPED_SOUL_FRAGMENTS_MULTIPLIER = new RivenStat<>("droppedSoulFragmentsMultiplier", DataType.FLOAT, true, true, 6.5F, 0.025F, RollManager.exponentialFloat(0.01F, 2F, 5.25F), StatFormatter.standardPercentage, RivenHost.TOTEM);
    public static RivenStat<Integer> TOTEM_DROPPED_SOUL_FRAGMENTS_PER_KILL = new RivenStat<>("droppedSoulFragments", DataType.INT, true, true, 6.5F, 0.035F, RollManager.linearInt(1, 10), StatFormatter.standardInt, RivenHost.TOTEM);
    public static RivenStat<Float> TOTEM_MAXIMUM_DAMAGE = new RivenStat<>("maximumDamage", DataType.FLOAT, true, true, 6.5F, 0.035F, RollManager.linearFloat(1, 10), StatFormatter.standardFloat, RivenHost.TOTEM);
    public static RivenStat<Float> TOTEM_MAXIMUM_DAMAGE_AMPLIFIER = new RivenStat<>("maximumDamageAmplifier", DataType.FLOAT, true, true, 15.5F, 0.010F, RollManager.exponentialFloat(0.01F, 1.25F, 8F), StatFormatter.standardPercentage, RivenHost.TOTEM);
    public static RivenStat<Float> TOTEM_INVULNERABLE_TIME = new RivenStat<>("invulnerableTime", DataType.FLOAT, true, true, 9.5F, 0.0085F, RollManager.exponentialFloat(0.01F, 0.5F, 5.0F), StatFormatter.timeFormatter, RivenHost.TOTEM);
    public static RivenStat<Float> TOTEM_EFFECT_TIME = new RivenStat<>("effectTime", DataType.FLOAT, true, true, 2.5F, 0.15F, RollManager.exponentialFloat(0.01F, 10F, 5.0F), StatFormatter.timeFormatter, RivenHost.TOTEM);
    public static RivenStat<Integer> TOTEM_REGENERATION_AMPLIFIER = new RivenStat<>("regenerationAmplifier", DataType.INT, true, true, 2.5F, 0.05F, RollManager.linearInt(1, 2), StatFormatter.standardInt, RivenHost.TOTEM);
    public static RivenStat<Integer> TOTEM_ABSORPTION_AMPLIFIER = new RivenStat<>("absorptionAmplifier", DataType.INT, true, true, 2.5F, 0.05F, RollManager.linearInt(1, 2), StatFormatter.standardInt, RivenHost.TOTEM);
    public static RivenStat<Integer> TOTEM_RESISTANCE_AMPLIFIER = new RivenStat<>("resistanceAmplifier", DataType.INT, true, true, 16.5F, 0.004F, RollManager.linearInt(1, 1), StatFormatter.standardInt, RivenHost.TOTEM);
    public static RivenStat<Integer> TOTEM_HEALTH_BOOST_AMPLIFIER = new RivenStat<>("healthBoostAmplifier", DataType.INT, true, true, 5.5F, 0.009F, RollManager.linearInt(1, 2), StatFormatter.standardInt, RivenHost.TOTEM);
    public static RivenStat<Float> TOTEM_REVIVE_COOLDOWN = new RivenStat<>("totemReviveCooldown", DataType.FLOAT, true, true, 2.5F, 0.05F, RollManager.linearFloat(1, 5), StatFormatter.standardFloat, RivenHost.TOTEM);


    public static RivenStat<Float> SHIELD_AMOUNT = new RivenStat<>("shieldAmount", DataType.FLOAT, true, true, 7.5F, 0.05F, RollManager.exponentialFloat(1F, 50F, 4.0F), StatFormatter.standardFloat, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_AMOUNT_AMPLIFIER = new RivenStat<>("shieldAmountAmplifier", DataType.FLOAT, true, true, 9.5F, 0.008F, RollManager.exponentialFloat(0.01F, 1F, 5.5F), StatFormatter.standardPercentage, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_FLAT_DAMAGE_REDUCTION = new RivenStat<>("shieldFlatReduction", DataType.FLOAT, true, true, 11F, 0.05F, RollManager.exponentialFloat(0.01F, 3F, 7.5F), StatFormatter.standardFloat, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_PERCENTAGE_DAMAGE_REDUCTION = new RivenStat<>("shieldPercentageReduction", DataType.FLOAT, true, true, 15F, 0.005F, RollManager.exponentialFloat(0.01F, 0.1F, 7.5F), StatFormatter.standardPercentage, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_RECOVERY_BASE = new RivenStat<>("shieldRecoveryBase", DataType.FLOAT, true, true, 8F, 0.01F, RollManager.exponentialFloat(0.01F, 5F, 7.5F), StatFormatter.standardFloat, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_RECOVERY_AMPLIFIER = new RivenStat<>("shieldRecoveryAmplifier", DataType.FLOAT, true, true, 15F, 0.001F, RollManager.exponentialFloat(0.01F, 0.5F, 7.5F), StatFormatter.standardPercentage, RivenHost.PROTECTOR);
    public static RivenStat<Float> SHIELD_DAMAGE_REFLECTION = new RivenStat<>("shieldReflection", DataType.FLOAT, true, true, 15F, 0.005F, RollManager.exponentialFloat(1F, 3F, 4.5F), StatFormatter.standardFloat, RivenHost.PROTECTOR);


    // Other
    public static RivenStat<Float> KNOCKBACK_MULTIPLIER = new RivenStat<>("knockbackMultiplier", DataType.FLOAT, true, true, 1.15F, 0.1F, RollManager.exponentialFloat(0.01F, 2.0F, 3.0F), StatFormatter.standardPercentage, RivenHost.WEAPON, RivenHost.ARMOR);

    public static Collection<RivenStat<?>> getRegisteredRivenStats() {
        return statRegistry.values();
    }

    public static List<RivenStat<?>> getAvailableStats(List<RivenHost> hosts) {
        Collection<RivenStat<?>> rivenStats = getRegisteredRivenStats();
        List<RivenStat<?>> compatibleStats = new ArrayList<>();

        for(RivenStat<?> rivenStat : rivenStats) {
            boolean l = false;
            for(RivenHost host : hosts) {
                if(rivenStat.getCompatibleHosts().contains(host)) {
                    l = true;
                    compatibleStats.add(rivenStat);
                    break;
                }
            }
        }

        return compatibleStats;
    }

    private static CompoundTag removeCurrentStatsIfPresent(ItemStack riven) {
        CompoundTag parent = riven.getOrCreateTag();
        if(parent.contains(TAG_MODIFIERS)) {
            parent.remove(TAG_MODIFIERS);
        }
        return parent;
    }

    public static RivenStat<?> rollSingularStat(List<RivenStat<?>> availableStats) {
        if(availableStats.size() == 0) throw new IllegalStateException("Error empty list in recursive method!");
        for(RivenStat<?> stat : availableStats) {
            if(Math.random() < stat.getChance()) {
                return stat;
            }
        }
        return rollSingularStat(availableStats);
    }

    public static void generateRiven(ItemStack rivenItem, List<RivenHost> rivenHosts) {
        List<RivenStat<?>> availableStats = getAvailableStats(rivenHosts);

        CompoundTag parent = removeCurrentStatsIfPresent(rivenItem);

        CompoundTag modifiers = new CompoundTag();
        float worth = 0.5F;
        int statAmount = NikesMath.randomInt(1, 4);
        float negativeStatBoost = 1.0F;

        for(int i = 0; i < statAmount; i++) {
            RivenStat<Object> stat = (RivenStat<Object>) rollSingularStat(availableStats);
            availableStats.remove(stat);
            RollManager<Object> rollManager = stat.getRollManager();
            Object rolledValue = rollManager.roll();

            if(rollManager instanceof RollManager.MinMaxManager<?>) {
                worth += stat.getWorth() * ((RollManager.MinMaxManager<Object>) stat.getRollManager()).ratio(rolledValue);
            } else worth += stat.getWorth();

            if(rollManager instanceof RollManager.MinMaxManager<?> && stat.isInvertAble() && Math.random() < 0.2F) {
                negativeStatBoost += stat.getWorth() * ((RollManager.MinMaxManager<Object>) stat.getRollManager()).ratio(rolledValue);
                rolledValue = stat.getDataType().multiply(rolledValue, -1F);
            }

            stat.getDataType().serialize(modifiers, stat.getRegistryName(), rolledValue);
        }

        RivenItem.setRivenValue(rivenItem, worth);
        if(statAmount < 3) {
            RivenItem.setRivenStatMult(rivenItem, (1 + (0.25F / statAmount)));
        }

        if(negativeStatBoost > 0) {
            RivenItem.setRivenStatMult(rivenItem, RivenItem.getRivenStatMult(rivenItem) * (1 + 0.250F * negativeStatBoost));
        }

        parent.put(TAG_MODIFIERS, modifiers);
    }

    public static HashMap<RivenStat<?>, Object> getTotalStats(ItemStack stack) {
        if(cache.containsKey(stack)) return cache.get(stack);
        if(hasModifiers(stack)) {
            HashMap<RivenStat<?>, Object> map = new HashMap<>();
            CompoundTag nbt = stack.getOrCreateTag();
            CompoundTag stats = nbt.getCompound(TAG_STATS);
            for(String registryName : stats.getAllKeys()) {
                RivenStats.getRivenStat(registryName).ifPresent(rivenStat -> {
                    map.put(rivenStat, rivenStat.getDataType().deserialize(stats, registryName));
                });
            }
            return map;
        } else return null;
    }



}
