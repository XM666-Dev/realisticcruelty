package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;

public class ClientGoreHandler {
    private static Map<ParticleType<?>, ParticleProvider<?>> particleProviders;

    public static void init(IEventBus eventBus) {
        if (!MixinConfig.CLIENT_GORE_ENABLED.get()) return;

        eventBus.addListener(ClientGoreHandler::onFMLClientSetupEvent);
    }

    public static void onFMLClientSetupEvent(FMLClientSetupEvent event) {
        var mc = Minecraft.getInstance();
        particleProviders = new HashMap<>();
        for (var entry : mc.particleEngine.providers.entrySet()) {
            var particle = BuiltInRegistries.PARTICLE_TYPE.get(entry.getKey());
            var provider = entry.getValue();
            particleProviders.put(particle, provider);
        }
    }

    public static ParticleProvider<?> getProvider(ParticleType<?> type) {
        return particleProviders.get(type);
    }
}
