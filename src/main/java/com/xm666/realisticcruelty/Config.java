package com.xm666.realisticcruelty;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue BLOOD_AMOUNT_FACTOR = BUILDER
            .defineInRange("blood_amount_factor", 0.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("blood_amount_max", 16.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_AMOUNT_ADDITION = BUILDER
            .defineInRange("blood_amount_addition", -0.5, Double.MIN_VALUE, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_SPEED_FACTOR = BUILDER
            .defineInRange("blood_speed_factor", 0.3, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_SPEED_MAX = BUILDER
            .defineInRange("blood_speed_max", 1.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_MELEE_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_melee_speed_min_multiplier", 0.1, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_MELEE_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_melee_speed_max_multiplier", 0.4, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_MELEE_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_melee_spread_degrees", 90.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue BLOOD_MELEE_FOG_ENABLED = BUILDER
            .define("blood_melee_fog_enabled", false);

    public static final ForgeConfigSpec.DoubleValue BLOOD_PROJECTILE_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_projectile_speed_min_multiplier", 0.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_PROJECTILE_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_projectile_speed_max_multiplier", 1.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_PROJECTILE_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_projectile_spread_degrees", 60.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue BLOOD_PROJECTILE_FOG_ENABLED = BUILDER
            .define("blood_projectile_fog_enabled", true);

    public static final ForgeConfigSpec.DoubleValue BLOOD_EXPLOSION_SPEED_MIN_MULTIPLIER = BUILDER
            .defineInRange("blood_explosion_speed_min_multiplier", 0.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_EXPLOSION_SPEED_MAX_MULTIPLIER = BUILDER
            .defineInRange("blood_explosion_speed_max_multiplier", 1.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_EXPLOSION_SPREAD_DEGREES = BUILDER
            .defineInRange("blood_explosion_spread_degrees", 75.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue BLOOD_EXPLOSION_FOG_ENABLED = BUILDER
            .define("blood_explosion_fog_enabled", true);

    public static final ForgeConfigSpec.BooleanValue BLOOD_SPLAT_ENABLED = BUILDER
            .define("blood_splat_enabled", true);

    public static final ForgeConfigSpec.IntValue BLOOD_SPLAT_LIFETIME = BUILDER
            .defineInRange("blood_splat_lifetime", 80, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_FOG_SIZE_FACTOR = BUILDER
            .defineInRange("blood_fog_size_factor", 0.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue BLOOD_FOG_SIZE_MAX = BUILDER
            .defineInRange("blood_fog_size_max", 1.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BLOOD_SPLASH_COUNT_MIN = BUILDER
            .defineInRange("blood_splash_count_min", 2, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BLOOD_SPLASH_COUNT_MAX = BUILDER
            .defineInRange("blood_splash_count_max", 4, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_BLACKLIST = BUILDER
            .defineList("gore_blacklist", List.of(), Config::isValidEntity);

    public static final ForgeConfigSpec.BooleanValue GORE_USE_WHITELIST = BUILDER
            .define("gore_use_whitelist", false);

    public static final ForgeConfigSpec.IntValue GORE_COLOR_DEFAULT = BUILDER
            .defineInRange("gore_color_default", 0x991F1F, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_COLORS = BUILDER
            .defineList("gore_colors", List.of(
                    "slime,0x5c993d",
                    "magma_cube,0x99471f",
                    "enderman,0x4d1f4d",
                    "endermite,0x4d1f4d",
                    "warden,0x144b66",
                    "glow_squid,0x33ffcc"
            ), Config::isValidEntityColor);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_TEXTURE_ITEMS = BUILDER
            .defineList("gore_texture_items", List.of(
                    "skeleton,bone",
                    "skeleton_horse,bone",
                    "wither_skeleton,coal",
                    "stray,bone",
                    "bogged,bone",
                    "blaze,blaze_rod",
                    "breeze,breeze_rod",
                    "shulker,shulker_shell",
                    "iron_golem,iron_nugget",
                    "snow_golem,snowball"
            ), Config::isValidEntityItem);

    public static final ForgeConfigSpec.ConfigValue<String> BLOOD_SOUND = BUILDER
            .define("blood_sound", "block.beehive.drip");

    public static final ForgeConfigSpec.DoubleValue BLOOD_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("blood_volume_multiplier", 1.0, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> BLOOD_SPLASH_SOUND = BUILDER
            .define("blood_splash_sound", "block.beehive.drip");

    public static final ForgeConfigSpec.DoubleValue BLOOD_SPLASH_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("blood_splash_volume_multiplier", 0.5, 0.0, Double.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<String> FRAGMENT_SOUND = BUILDER
            .define("fragment_sound", "block.dripstone_block.fall");

    public static final ForgeConfigSpec.DoubleValue FRAGMENT_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("fragment_volume_multiplier", 1.0, 0.0, Double.MAX_VALUE);

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void init(ModContainer container) {
        registerConfig(ModConfig.Type.COMMON, SPEC, container);
    }

    public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, ModContainer container) {
        registerConfig(type, spec, container, type.extension());
    }

    public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, ModContainer container, String extension) {
        var fileName = String.format(Locale.ROOT, "%s-%s.toml", RealisticCruelty.MODID, extension);
        var config = new ModConfig(type, spec, container, fileName);
        try {
            var method = ConfigTracker.class.getDeclaredMethod("openConfig", ModConfig.class, Path.class);
            method.setAccessible(true);
            method.invoke(ConfigTracker.INSTANCE, config, FMLPaths.CONFIGDIR.get());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] splitPair(String string) {
        var index = string.indexOf(',');
        var first = string.substring(0, index);
        var second = string.substring(index + 1);
        return new String[]{first, second};
    }

    private static boolean isValidEntity(Object object) {
        try {
            var path = (String) object;
            var key = ResourceLocation.parse(path);
            return BuiltInRegistries.ENTITY_TYPE.containsKey(key);
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidItem(Object object) {
        try {
            var path = (String) object;
            var key = ResourceLocation.parse(path);
            return BuiltInRegistries.ITEM.containsKey(key);
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidEntityColor(Object object) {
        try {
            var pair = (String) object;
            var strings = splitPair(pair);
            var entity = strings[0];
            if (!isValidEntity(entity)) return false;

            Integer.decode(strings[1]);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidEntityItem(Object object) {
        try {
            var pair = (String) object;
            var strings = splitPair(pair);
            var entity = strings[0];
            if (!isValidEntity(entity)) return false;

            var texture = strings[1];
            return isValidItem(texture);
        } catch (Exception exception) {
            return false;
        }
    }
}
