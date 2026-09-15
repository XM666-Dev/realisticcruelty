package com.xm666.realisticcruelty.math;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class Random {
    private static final RandomSource random = RandomSource.create();

    public static float nextFloat() {
        return random.nextFloat();
    }

    public static float nextFloat(float max) {
        return random.nextFloat() * max;
    }

    public static float nextFloat(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    public static double nextDouble() {
        return random.nextDouble();
    }

    public static double nextDouble(double max) {
        return random.nextDouble() * max;
    }

    public static double nextDouble(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    public static float nextAngle(float spread) {
        var spreadAngle = Mth.DEG_TO_RAD * spread;
        return Random.nextFloat(-spreadAngle, spreadAngle);
    }
}
