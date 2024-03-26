package de.nike.terraprotector.stats;

import de.nike.terraprotector.lib.NikesMath;

import java.util.concurrent.ThreadLocalRandom;

public abstract class RollManager<T> {

    public static RollManager<Float> linearFloat(float min, float max) {
        return new LinearFloat(min, max);
    }

    public static RollManager<Integer> linearInt(int min, int max) {
        return new LinearInt(min, max);
    }

    public static RollManager<Float> exponentialFloat(float min, float max, double lambda) {
        return new ExponentialFloat(min, max, lambda);
    }

    final DataType<T> type;

    public RollManager(DataType<T> type) {
        this.type = type;
    }

    public abstract T roll();


    public abstract static class MinMaxManager<E> extends RollManager<E> {
        public MinMaxManager(DataType<E> type) {
            super(type);
        }

        public abstract E getMin();
        public abstract E getMax();
        public abstract float ratio(E value);


    }

    public static class LinearFloat extends MinMaxManager<Float> {

        final float min;
        final float max;

        public LinearFloat(float min, float max) {
            super(DataType.FLOAT);
            this.min = min;
            this.max = max;
        }

        @Override
        public Float roll() {
            return NikesMath.randomFloat(min, max);
        }

        public Float getMax() {
            return max;
        }

        @Override
        public float ratio(Float value) {
            return Math.max(0F, Math.min(1.0F, (value - min) / max));
        }

        public Float getMin() {
            return min;
        }

    }

    public static class LinearInt extends MinMaxManager<Integer> {

        final int min;
        final int max;

        public LinearInt(int min, int max) {
            super(DataType.INT);
            this.min = min;
            this.max = max;
        }

        @Override
        public Integer roll() {
            return NikesMath.randomInt(min, max);
        }

        @Override
        public Integer getMin() {
            return min;
        }

        @Override
        public Integer getMax() {
            return max;
        }

        @Override
        public float ratio(Integer value) {
            return Math.max(0F, Math.min(1.0F, (float) (value - min) / max));
        }
    }

    public static class ExponentialFloat extends MinMaxManager<Float> {

        final float min;
        final float max;
        final double lambda;

        public ExponentialFloat(float min, float max, double lambda) {
            super(DataType.FLOAT);
            this.min = min;
            this.max = max;
            this.lambda = lambda;
        }

        @Override
        public Float getMin() {
            return min;
        }

        @Override
        public Float getMax() {
            return max;
        }

        @Override
        public float ratio(Float value) {
            return Math.max(0F, Math.min(1.0F, (value - min) / max));
        }

        @Override
        public Float roll() {
            double u = ThreadLocalRandom.current().nextDouble();
            return Math.max(min, Math.min(max, (float) (min + (-Math.log(1.0 - u) / lambda) * (max - min))));
        }
    }

}
