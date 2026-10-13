package com.xm666.realisticcruelty;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(value = RealisticCruelty.MODID, dist = Dist.CLIENT)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_FACTOR = BUILDER
            .defineInRange("blood_amount_factor", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_ADDITION = BUILDER
            .defineInRange("blood_amount_addition", -0.5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("blood_amount_max", 16.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_FACTOR = BUILDER
            .defineInRange("blood_speed_factor", 0.3, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_ADDITION = BUILDER
            .defineInRange("blood_speed_addition", 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_MAX = BUILDER
            .defineInRange("blood_speed_max", 1.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_GENERAL_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_general_speed_min_multiplier", 0.2, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_GENERAL_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_general_speed_max_multiplier", 0.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_GENERAL_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_general_spread_degrees", 90.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_GENERAL_FOG_ENABLED = BUILDER
            .define("blood_general_fog_enabled", true);

    public static final ModConfigSpec.DoubleValue BLOOD_MELEE_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_melee_speed_min_multiplier", 0.2, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_MELEE_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_melee_speed_max_multiplier", 0.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_MELEE_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_melee_spread_degrees", 90.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_MELEE_FOG_ENABLED = BUILDER
            .define("blood_melee_fog_enabled", true);

    public static final ModConfigSpec.DoubleValue BLOOD_PROJECTILE_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_projectile_speed_min_multiplier", 0.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_PROJECTILE_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_projectile_speed_max_multiplier", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_PROJECTILE_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_projectile_spread_degrees", 60.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_PROJECTILE_FOG_ENABLED = BUILDER
            .define("blood_projectile_fog_enabled", true);

    public static final ModConfigSpec.DoubleValue BLOOD_EXPLOSION_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_explosion_speed_min_multiplier", 0.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_EXPLOSION_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_explosion_speed_max_multiplier", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_EXPLOSION_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_explosion_spread_degrees", 75.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_EXPLOSION_FOG_ENABLED = BUILDER
            .define("blood_explosion_fog_enabled", true);

    public static final ModConfigSpec.DoubleValue BLOOD_DEPTH_OFFSET = BUILDER
            .defineInRange("blood_depth_offset", 0.125, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_DEPTH_OFFSET = BUILDER
            .defineInRange("blood_fog_depth_offset", 0.25, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_SPLAT_ENABLED = BUILDER
            .define("blood_splat_enabled", true);

    public static final ModConfigSpec.IntValue BLOOD_SPLAT_LIFETIME = BUILDER
            .defineInRange("blood_splat_lifetime", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_FACTOR = BUILDER
            .defineInRange("blood_fog_size_factor", 0.2, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_ADDITION = BUILDER
            .defineInRange("blood_fog_size_addition", 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_MAX = BUILDER
            .defineInRange("blood_fog_size_max", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.IntValue BLOOD_SPLASH_COUNT_MIN = BUILDER
            .defineInRange("blood_splash_count_min", 2, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BLOOD_SPLASH_COUNT_MAX = BUILDER
            .defineInRange("blood_splash_count_max", 3, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> BLOOD_SOUND = BUILDER
            .define("blood_sound", "block.beehive.drip");

    public static final ModConfigSpec.DoubleValue BLOOD_VOLUME_MULTIPLIER_MIN = BUILDER
            .defineInRange("blood_volume_multiplier_min", 0.3, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_VOLUME_MULTIPLIER_MAX = BUILDER
            .defineInRange("blood_volume_multiplier_max", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.ConfigValue<String> FRAGMENT_SOUND = BUILDER
            .define("fragment_sound", "block.dripstone_block.fall");

    public static final ModConfigSpec.DoubleValue FRAGMENT_VOLUME_MULTIPLIER_MIN = BUILDER
            .defineInRange("fragment_volume_multiplier_min", 0.3, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue FRAGMENT_VOLUME_MULTIPLIER_MAX = BUILDER
            .defineInRange("fragment_volume_multiplier_max", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue CLIENT_GORE_AMOUNT_MIN = BUILDER
            .defineInRange("client_gore_amount_min", 4.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue CLIENT_GORE_AMOUNT_MAX = BUILDER
            .defineInRange("client_gore_amount_max", 8.0, 0.0, Double.POSITIVE_INFINITY);

    private static final ModConfigSpec SPEC = BUILDER.build();

    public ClientConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
