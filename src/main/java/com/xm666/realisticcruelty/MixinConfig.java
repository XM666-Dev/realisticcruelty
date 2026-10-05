package com.xm666.realisticcruelty;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MixinConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CLIENT_GORE_ENABLED = BUILDER
            .define("clientGoreEnabled", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
