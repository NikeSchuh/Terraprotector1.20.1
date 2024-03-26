package de.nike.terraprotector.lib;

import net.minecraft.ChatFormatting;

import java.awt.*;

public class ColorUtil {

    public static int fade(Color from, Color to, int alpha, double ratio) {
        int red = (int)Math.abs((ratio * to.getRed()) + ((1 - ratio) * from.getRed()));
        int green = (int)Math.abs((ratio * to.getGreen()) + ((1 - ratio) * from.getGreen()));
        int blue = (int)Math.abs((ratio * to.getBlue()) + ((1 - ratio) * from.getBlue()));
        return new Color(red, green, blue, alpha).getRGB();
    }

    public static int fade(Color from, Color to, double ratio) {
        int red = (int)Math.abs((ratio * to.getRed()) + ((1 - ratio) * from.getRed()));
        int green = (int)Math.abs((ratio * to.getGreen()) + ((1 - ratio) * from.getGreen()));
        int blue = (int)Math.abs((ratio * to.getBlue()) + ((1 - ratio) * from.getBlue()));
        return new Color(red, green, blue).getRGB();
    }

    public static int fastFade(int from, int to, double ratio) {
        int fromRed = (from >> 16) & 0xFF;
        int fromGreen = (from >> 8) & 0xFF;
        int fromBlue = (from >> 0) & 0xFF;

        int toRed = (to >> 16) & 0xFF;
        int toGreen = (to >> 8) & 0xFF;
        int toBlue = (to >> 0) & 0xFF;

        int red = (int)Math.abs((ratio * toRed) + ((1 - ratio) * fromRed));
        int green = (int)Math.abs((ratio * toGreen) + ((1 - ratio) * fromGreen));
        int blue = (int)Math.abs((ratio * toBlue) + ((1 - ratio) * fromBlue));

        return ((255 & 0xFF) << 24) |
                ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8)  |
                ((blue & 0xFF) << 0);
    }

    public static int test(Color startColor, Color endColor, double fraction) {
        fraction = Math.min(1.0f, fraction);
        int red = (int)(fraction * endColor.getRed() +
                (1 - fraction) * startColor.getRed());
        int green = (int)(fraction * endColor.getGreen() +
                (1 - fraction) * startColor.getGreen());
        int blue = (int)(fraction * endColor.getBlue() +
                (1 - fraction) * startColor.getBlue());
        return new Color(red, green, blue).getRGB();
    }

    public static String rainbowWaveTextEffect(String text, long elapsedTicks, float speedMult) {
        StringBuilder result = new StringBuilder();
        elapsedTicks*=speedMult;
        int length = text.length();
        int hueStep = 360 / length;

        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            int hue = (int) ((elapsedTicks / 10 + i * hueStep) % 360);
            ChatFormatting color = getRainbowColor(hue);
            result.append(color).append(c);
        }

        return result.toString();
    }

    private static ChatFormatting getRainbowColor(int hue) {
        int colorIndex = hue / 60;
        switch (colorIndex) {
            case 0: return ChatFormatting.RED;
            case 1: return ChatFormatting.GOLD;
            case 2: return ChatFormatting.YELLOW;
            case 3: return ChatFormatting.GREEN;
            case 4: return ChatFormatting.AQUA;
            default: return ChatFormatting.LIGHT_PURPLE;
        }
    }

}
