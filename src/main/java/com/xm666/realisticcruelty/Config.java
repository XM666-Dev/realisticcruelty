package com.xm666.realisticcruelty;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@EventBusSubscriber(modid = RealisticCruelty.MODID)
@Mod(RealisticCruelty.MODID)
public class Config {
    public static final HashSet<EntityType<?>> goreBlacklist = new HashSet<>();

    public static final HashMap<EntityType<?>, Integer> goreColors = new HashMap<>();

    public static final HashMap<EntityType<?>, Integer> goreTextureItems = new HashMap<>();

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_FACTOR = BUILDER
            .defineInRange("blood_amount_factor", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("blood_amount_max", 16.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_AMOUNT_ADDITION = BUILDER
            .defineInRange("blood_amount_addition", -0.5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_FACTOR = BUILDER
            .defineInRange("blood_speed_factor", 0.3, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_SPEED_MAX = BUILDER
            .defineInRange("blood_speed_max", 1.5, 0.0, Double.POSITIVE_INFINITY);

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

    public static final ModConfigSpec.DoubleValue BLOOD_Z_OFFSET = BUILDER
            .defineInRange("blood_z_offset", 0.5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.BooleanValue BLOOD_SPLAT_ENABLED = BUILDER
            .define("blood_splat_enabled", true);

    public static final ModConfigSpec.IntValue BLOOD_SPLAT_LIFETIME = BUILDER
            .defineInRange("blood_splat_lifetime", 100, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_FACTOR = BUILDER
            .defineInRange("blood_fog_size_factor", 0.3, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue BLOOD_FOG_SIZE_MAX = BUILDER
            .defineInRange("blood_fog_size_max", 1.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.IntValue BLOOD_SPLASH_COUNT_MIN = BUILDER
            .defineInRange("blood_splash_count_min", 2, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue BLOOD_SPLASH_COUNT_MAX = BUILDER
            .defineInRange("blood_splash_count_max", 3, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> GORE_BLACKLIST = BUILDER
            .defineList("gore_blacklist", List.of(), () -> "", Config::isValidEntity);

    public static final ModConfigSpec.BooleanValue GORE_USE_WHITELIST = BUILDER
            .define("gore_use_whitelist", false);

    public static final ModConfigSpec.IntValue GORE_COLOR_DEFAULT = BUILDER
            .defineInRange("gore_color_default", 0x991F1F, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> GORE_COLORS = BUILDER
            .defineList("gore_colors", List.of(
                    "slime,0x70cc52",
                    "magma_cube,0x99471f",
                    "enderman,0x4d1f4d",
                    "endermite,0x4d1f4d",
                    "warden,0x144b66",
                    "glow_squid,0x33ffcc",
                    "wither_skeleton,0x262626"
            ), () -> "", Config::isValidEntityColor);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> GORE_TEXTURE_ITEMS = BUILDER
            .defineList("gore_texture_items", List.of(
                    "skeleton,bone",
                    "skeleton_horse,bone",
                    "wither_skeleton,bone",
                    "stray,bone",
                    "bogged,bone",
                    "blaze,blaze_rod",
                    "breeze,breeze_rod",
                    "shulker,shulker_shell",
                    "iron_golem,iron_nugget",
                    "snow_golem,snowball"
            ), () -> "", Config::isValidEntityItem);

    public static final ModConfigSpec.ConfigValue<String> BLOOD_SOUND = BUILDER
            .define("blood_sound", "block.beehive.drip");

    public static final ModConfigSpec.DoubleValue BLOOD_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("blood_volume_multiplier", 1.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.ConfigValue<String> BLOOD_SPLASH_SOUND = BUILDER
            .define("blood_splash_sound", "block.beehive.drip");

    public static final ModConfigSpec.DoubleValue BLOOD_SPLASH_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("blood_splash_volume_multiplier", 0.5, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.ConfigValue<String> FRAGMENT_SOUND = BUILDER
            .define("fragment_sound", "block.dripstone_block.fall");

    public static final ModConfigSpec.DoubleValue FRAGMENT_VOLUME_MULTIPLIER = BUILDER
            .defineInRange("fragment_volume_multiplier", 1.0, 0.0, Double.POSITIVE_INFINITY);

    private static final ModConfigSpec SPEC = BUILDER.build();

    public Config(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onLoading(ModConfigEvent.Loading event) {
        load();
    }

    @SubscribeEvent
    public static void onLoading(ModConfigEvent.Reloading event) {
        load();
    }

    private static void load() {
        goreBlacklist.clear();
        for (var string : GORE_BLACKLIST.get()) {
            goreBlacklist.add(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(string)));
        }
        goreColors.clear();
        for (var string : GORE_COLORS.get()) {
            var pair = Config.splitPair(string);
            var entity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(pair[0]));
            var color = Integer.decode(pair[1]);
            goreColors.put(entity, color);
        }
        goreTextureItems.clear();
        for (var string : GORE_TEXTURE_ITEMS.get()) {
            var pair = Config.splitPair(string);
            var entity = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(pair[0]));
            var item = BuiltInRegistries.ITEM.getId(ResourceLocation.parse(pair[1]));
            goreTextureItems.put(entity, item);
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
            var string = (String) object;
            var entity = ResourceLocation.parse(string);
            return BuiltInRegistries.ENTITY_TYPE.containsKey(entity);
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidItem(Object object) {
        try {
            var string = (String) object;
            var entity = ResourceLocation.parse(string);
            return BuiltInRegistries.ITEM.containsKey(entity);
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidEntityColor(Object object) {
        try {
            var string = (String) object;
            var pair = splitPair(string);
            var entity = pair[0];
            if (!isValidEntity(entity)) return false;

            Integer.decode(pair[1]);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private static boolean isValidEntityItem(Object object) {
        try {
            var string = (String) object;
            var pair = splitPair(string);
            var entity = pair[0];
            if (!isValidEntity(entity)) return false;

            var item = pair[1];
            return isValidItem(item);
        } catch (Exception exception) {
            return false;
        }
    }
}
