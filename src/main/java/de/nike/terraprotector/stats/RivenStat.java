package de.nike.terraprotector.stats;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

public class RivenStat<S> {


    private final String registryName;
    private final boolean invertAble;
    private final float worth;
    private final float chance;
    private final RollManager<S> rollManager;
    private final List<RivenHost> compatibleHosts;
    private final DataType<S> dataType;
    private final boolean combineAble;
    private final StatFormatter<S> statFormatter;

    public RivenStat(String registryName, DataType<S> dataType, boolean combineAble, boolean invertable, float value, float chance, RollManager<S> rollType, StatFormatter<S> formatter, RivenHost... hosts) {
        this.registryName = registryName;
        this.dataType = dataType;
        this.combineAble = combineAble;
        this.invertAble = invertable;
        this.worth = value;
        this.chance = chance;
        this.rollManager = rollType;
        this.compatibleHosts = Arrays.asList(hosts);
        this.statFormatter = formatter;
        RivenStats.register(this);
    }

    public String getRegistryName() {
        return registryName;
    }

    public boolean isInvertAble() {
        return invertAble;
    }

    public float getWorth() {
        return worth;
    }

    public float getChance() {
        return chance;
    }

    public RollManager<S> getRollManager() {
        return rollManager;
    }

    public DataType<S> getDataType() {
        return dataType;
    }

    public StatFormatter<S> getStatFormatter() {
        return statFormatter;
    }

    public List<RivenHost> getCompatibleHosts() {
        return compatibleHosts;
    }
}
