package de.nike.terraprotector.stats;

public abstract class StatFormatter<E> {

    public static StatFormatter<Float> standardPercentage = new StatFormatter<Float>() {
        @Override
        public String format(Object val2) {
            float val = (Float) val2;
            return val > 0 ? "+"+ (String.format("%.2f", (val * 100.0F)) + "%") : (String.format("%.2f", (val * 100.0F)) + "%");
        }
    };

    public static StatFormatter<Float> standardFloat = new StatFormatter<Float>() {
        @Override
        public String format(Object val2) {
            float val = (Float) val2;
            return val > 0 ? "+"+ (String.format("%.2f", val)) : (String.format("%.2f", val));
        }
    };

    public static StatFormatter<Float> timeFormatter = new StatFormatter<>() {
        @Override
        public String format(Object val2) {
            float val = (Float) val2;
            return val > 0 ? "+" + (String.format("%.2f", val) + "s") : (String.format("%.2f", val) + "s");
        }
    };

    public static StatFormatter<Integer> standardInt = new StatFormatter<Integer>() {
        @Override
        public String format(Object val2) {
            int val = (Integer) val2;
            return val > 0 ? "+" + val : String.valueOf(val);
        }
    };

    public abstract String format(Object val);

}
