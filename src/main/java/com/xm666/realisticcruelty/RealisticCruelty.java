package com.xm666.realisticcruelty;

import com.mojang.logging.LogUtils;
import com.xm666.realisticcruelty.network.PayloadHandler;
import com.xm666.realisticcruelty.particle.ParticleTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RealisticCruelty.MODID)
public class RealisticCruelty {
    public static final String MODID = "realisticcruelty";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RealisticCruelty(FMLJavaModLoadingContext context) {
        this(context, context.getModEventBus());
    }

    public RealisticCruelty(FMLJavaModLoadingContext context, IEventBus eventBus) {
        Config.init(context);
        ParticleTypes.init(eventBus);
        PayloadHandler.init();
    }
}
