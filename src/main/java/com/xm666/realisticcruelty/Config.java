package com.xm666.realisticcruelty;

import com.xm666.realisticcruelty.handler.KeyMap;
import com.xm666.realisticcruelty.handler.KeyParser;
import com.xm666.realisticcruelty.handler.KeySet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = RealisticCruelty.MODID)
public class Config {
    public static final KeySet<EntityType<?>> goreBlacklist = new KeySet<>(Registries.ENTITY_TYPE);

    public static final KeyMap<EntityType<?>, Integer> goreColors = new KeyMap<>(Registries.ENTITY_TYPE);

    public static final KeyMap<EntityType<?>, Integer> goreTextureItems = new KeyMap<>(Registries.ENTITY_TYPE);

    public static final KeySet<DamageType> goreGeneralDamageTypes = new KeySet<>(Registries.DAMAGE_TYPE);

    public static final KeySet<DamageType> goreExplosionDamageTypes = new KeySet<>(Registries.DAMAGE_TYPE);

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_BLACKLIST = BUILDER
            .defineListAllowEmpty("gore_blacklist", List.of(), KeyParser::validateKey);

    public static final ForgeConfigSpec.BooleanValue GORE_USE_WHITELIST = BUILDER
            .define("gore_use_whitelist", false);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_COLORS = BUILDER
            .defineListAllowEmpty("gore_colors", List.of(
                    "slime,0x70cc52",
                    "magma_cube,0x99471f",
                    "enderman,0x4d1f4d",
                    "endermite,0x4d1f4d",
                    "warden,0x144b66",
                    "glow_squid,0x33ffcc",
                    "wither_skeleton,0x262626"
            ), object -> KeyParser.validateValueKey(object, KeyParser::parseInteger));

    public static final ForgeConfigSpec.IntValue GORE_COLOR_DEFAULT = BUILDER
            .defineInRange("gore_color_default", 0x991F1F, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_TEXTURE_ITEMS = BUILDER
            .defineListAllowEmpty("gore_texture_items", List.of(
                    "skeleton,bone",
                    "skeleton_horse,bone",
                    "wither_skeleton,bone",
                    "stray,bone",
                    "bogged,bone",
                    "blaze,blaze_rod",
                    "breeze,breeze_rod",
                    "shulker,shulker_shell",
                    "iron_golem,iron_ingot",
                    "snow_golem,snowball"
            ), object -> KeyParser.validateValueKey(object, KeyParser::parseItemId));

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_GENERAL_DAMAGE_TYPES = BUILDER
            .defineListAllowEmpty("gore_general_damage_types", List.of(
                    "#neoforge:is_physical",
                    "#minecraft:is_explosion"
            ), KeyParser::validateKey);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GORE_EXPLOSION_DAMAGE_TYPES = BUILDER
            .defineListAllowEmpty("gore_explosion_damage_types", List.of(
                    "#minecraft:is_explosion"
            ), KeyParser::validateKey);

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void init(ModContainer container, IEventBus eventBus) {
        container.addConfig(new ModConfig(ModConfig.Type.COMMON, SPEC, container));
        eventBus.addListener(Config::onLoading);
        eventBus.addListener(Config::onReloading);
    }

    @SubscribeEvent
    public static void onLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() != ModConfig.Type.COMMON) return;

        load();
    }

    @SubscribeEvent
    public static void onReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() != ModConfig.Type.COMMON) return;

        load();
    }

    private static void load() {
        goreBlacklist.load(GORE_BLACKLIST.get());
        goreColors.load(GORE_COLORS.get(), KeyParser::parseInteger);
        goreTextureItems.load(GORE_TEXTURE_ITEMS.get(), KeyParser::parseItemId);
        goreGeneralDamageTypes.load(GORE_GENERAL_DAMAGE_TYPES.get());
        goreExplosionDamageTypes.load(GORE_EXPLOSION_DAMAGE_TYPES.get());
    }
}
