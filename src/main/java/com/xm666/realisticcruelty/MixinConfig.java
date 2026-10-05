package com.xm666.realisticcruelty;

import net.minecraftforge.common.ForgeConfigSpec;

public class MixinConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue CLIENT_GORE_ENABLED = BUILDER
            .define("clientGoreEnabled", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
