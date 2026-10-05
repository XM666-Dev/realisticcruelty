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

    public static final ModConfigSpec.DoubleValue CLIENT_BLOOD_AMOUNT_MIN = BUILDER
            .defineInRange("client_blood_amount_min", 2.0, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.DoubleValue CLIENT_BLOOD_AMOUNT_MAX = BUILDER
            .defineInRange("client_blood_amount_max", 8.0, 0.0, Double.POSITIVE_INFINITY);

    private static final ModConfigSpec SPEC = BUILDER.build();

    public ClientConfig(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
