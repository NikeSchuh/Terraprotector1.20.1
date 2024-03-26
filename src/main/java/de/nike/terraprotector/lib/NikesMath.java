package de.nike.terraprotector.lib;

import java.awt.geom.Point2D;
import java.util.concurrent.ThreadLocalRandom;

public class NikesMath {

    public static float lerp(float start, float end, float smoothSpeed) {
        return (end - start) * smoothSpeed + start;
    }
    public static double lerp(double start, double end, double smoothSpeed) {
        return (end - start) * smoothSpeed + start;
    }

    public static float clamp(float p_76131_0_, float p_76131_1_, float p_76131_2_) {
        if (p_76131_0_ < p_76131_1_) {
            return p_76131_1_;
        } else {
            return Math.min(p_76131_0_, p_76131_2_);
        }
    }


    public static int hsvToRgb(float p_181758_0_, float p_181758_1_, float p_181758_2_) {
        int i = (int)(p_181758_0_ * 6.0F) % 6;
        float f = p_181758_0_ * 6.0F - (float)i;
        float f1 = p_181758_2_ * (1.0F - p_181758_1_);
        float f2 = p_181758_2_ * (1.0F - f * p_181758_1_);
        float f3 = p_181758_2_ * (1.0F - (1.0F - f) * p_181758_1_);
        float f4;
        float f5;
        float f6;
        switch(i) {
            case 0:
                f4 = p_181758_2_;
                f5 = f3;
                f6 = f1;
                break;
            case 1:
                f4 = f2;
                f5 = p_181758_2_;
                f6 = f1;
                break;
            case 2:
                f4 = f1;
                f5 = p_181758_2_;
                f6 = f3;
                break;
            case 3:
                f4 = f1;
                f5 = f2;
                f6 = p_181758_2_;
                break;
            case 4:
                f4 = f3;
                f5 = f1;
                f6 = p_181758_2_;
                break;
            case 5:
                f4 = p_181758_2_;
                f5 = f1;
                f6 = f2;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
        }


        int j = clamp((int)(f4 * 255.0F), 0, 255);
        int k = clamp((int)(f5 * 255.0F), 0, 255);
        int l = clamp((int)(f6 * 255.0F), 0, 255);
        return j << 16 | k << 8 | l;
    }

    public static int clamp(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        if (p_76125_0_ < p_76125_1_) {
            return p_76125_1_;
        } else {
            return p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_;
        }
    }

    public static float randomFloat(float min, float max) {
        if (min >= max)
            throw new IllegalArgumentException("max must be greater than min");
        float result = ThreadLocalRandom.current().nextFloat() * (max - min) + min;
        if (result >= max) // correct for rounding
            result = Float.intBitsToFloat(Float.floatToIntBits(max) - 1);
        return result;
    }

    public static boolean isInside(float x, float y, float rX, float rY, float rW, float rH) {
        return (x >= rX && x <= rX + rW) && (y >= rY && y <= rY + rH);
    }

    public static boolean circleCull(float x, float y, float viewRadius, float objectX, float objectY, float objectRadius) {
        float distanceBetweenCenters = distance(x, y, objectX, objectY);
        float sumOfRadii = viewRadius + objectRadius;

        return distanceBetweenCenters > sumOfRadii;
    }

    public static boolean circleCull(double x, double y, double viewRadius, double objectX, double objectY, double objectRadius) {
        double distanceBetweenCenters = distance(x, y, objectX, objectY);
        double sumOfRadii = viewRadius + objectRadius;

        return distanceBetweenCenters > sumOfRadii;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        double px = x2 - x1;
        double py = y2 - y1;
        return Math.sqrt(px * px + py * py);
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        float px = x2 - x1;
        float py = y2 - y1;
        return (float) Math.sqrt(px * px + py * py);
    }

    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
    }


}

