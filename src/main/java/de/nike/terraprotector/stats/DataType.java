package de.nike.terraprotector.stats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class DataType<T> {

    public static final DataType<Integer> INT = new DataType<>(true) {
        @Override
        public Integer combine(Object first, Object second) {
            return (Integer) first + (Integer) second;
        }

        @Override
        public Integer multiply(Integer first, float second) {
            return (int) (first * second);
        }

        @Override
        public void serialize(CompoundTag tag, String key, Object value) {
            tag.putInt(key, (Integer) value);
        }

        @Override
        public Integer deserialize(CompoundTag tag, String key) {
            return tag.getInt(key);
        }
    };

    public static final DataType<Float> FLOAT = new DataType<Float>(true) {

        @Override
        public Float combine(Object first, Object second) {
            return (Float) first + (Float) second;
        }

        @Override
        public Float multiply(Float first, float second) {
            return first * second;
        }

        @Override
        public void serialize(CompoundTag tag, String key, Object value) {
            tag.putFloat(key, (Float) value);
        }

        @Override
        public Float deserialize(CompoundTag tag, String key) {
            return tag.getFloat(key);
        }
    };

    public static final DataType<MobEffect> MOB_EFFECT = new DataType<MobEffect>(false) {
        @Override
        public MobEffect combine(Object first, Object second) {
            return null;
        }

        @Override
        public MobEffect multiply(MobEffect first, float second) {
            return null;
        }

        @Override
        public void serialize(CompoundTag tag, String key, Object value) {
            tag.putString(key, ForgeRegistries.MOB_EFFECTS.getKey((MobEffect) value).toString());
        }

        @Override
        public MobEffect deserialize(CompoundTag tag, String key) {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString(key)));
        }
    };



    final boolean operateable;

    public DataType(boolean operateable) {
        this.operateable = operateable;
    }

    public abstract T combine(Object first, Object second);
    public abstract T multiply(T first, float second);
    public abstract void serialize(CompoundTag tag, String key, Object value);
    public boolean isOperateable() {
        return operateable;
    }

    public abstract T deserialize(CompoundTag tag, String key);
}
