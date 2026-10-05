package com.xm666.realisticcruelty;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue CLIENT_BLOOD_AMOUNT_MIN = BUILDER
            .defineInRange("client_blood_amount_min", 2.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ForgeConfigSpec.DoubleValue CLIENT_BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("client_blood_amount_max", 8.0, 0.0, Double.POSITIVE_INFINITY);

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    public static void init(ModContainer container) {
        container.addConfig(new ModConfig(ModConfig.Type.CLIENT, SPEC, container));
    }
}
