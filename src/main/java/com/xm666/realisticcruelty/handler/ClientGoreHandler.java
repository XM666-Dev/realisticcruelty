package com.xm666.realisticcruelty.handler;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;

import java.util.HashMap;
import java.util.Map;

public class ClientGoreHandler {
    private static final Map<ParticleType<?>, ParticleProvider<?>> particleProviders = new HashMap<>();

    public static void addProvider(ParticleType<?> type, ParticleProvider<?> provider) {
        particleProviders.put(type, provider);
    }

    public static ParticleProvider<?> getProvider(ParticleType<?> type) {
        return particleProviders.get(type);
    }
}
