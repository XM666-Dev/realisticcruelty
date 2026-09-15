package com.xm666.realisticcruelty;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(RealisticCruelty.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_MULTIPLIER = BUILDER
            .defineInRange("blood_amount_multiplier", 1.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("blood_amount_max", 16.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_FACTOR = BUILDER
            .defineInRange("blood_speed_factor", 0.3, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_MAX = BUILDER
            .defineInRange("blood_speed_max", 1.5, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_RAY_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_ray_spread_degrees", 60.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_SPHERE_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_sphere_spread_degrees", 60.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue BLOOD_SPLAT_ENABLED = BUILDER
            .define("blood_splat_enabled", true);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_FACTOR = BUILDER
            .defineInRange("blood_fog_size_factor", 0.5, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_MAX = BUILDER
            .defineInRange("blood_fog_size_max", 1.5, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue BLOOD_SPLASH_COUNT = BUILDER
            .defineInRange("blood_splash_count", 3, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_VOLUME = BUILDER
            .defineInRange("blood_volume", 1.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_SPLASH_VOLUME = BUILDER
            .defineInRange("blood_splash_volume", 0.5, 0.0, Double.MAX_VALUE);

    private static final ModConfigSpec SPEC = BUILDER.build();

    public Config(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
