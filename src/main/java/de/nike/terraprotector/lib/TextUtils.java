package de.nike.terraprotector.lib;

import net.minecraft.network.chat.Component;

public class TextUtils {

    public static Component string(String s) {
        return Component.literal(s);
    }
    public static String translation(String s) {return Component.translatable(s).getString();
    }

    public static String formatE(long value) {
        if (value < 1000) return "" + value;
        int exp = (int) (Math.log(value) / Math.log(1000));
        return String.format("%.1f%c",
                value / Math.pow(1000, exp),
                "KMGTPEZY".charAt(exp-1));
    }

    public static String formatE(double v) {
        long value = (long) v;
        if (value < 1000) return "" + value;
        int exp = (int) (Math.log(value) / Math.log(1000));
        return String.format("%.1f%c",
                value / Math.pow(1000, exp),
                "KMGTPEZY".charAt(exp-1));
    }

}

