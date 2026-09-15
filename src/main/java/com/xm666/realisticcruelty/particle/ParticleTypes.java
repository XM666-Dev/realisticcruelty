package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(RealisticCruelty.MODID)
@EventBusSubscriber(modid = RealisticCruelty.MODID)
public class ParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE,
            RealisticCruelty.MODID
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD = PARTICLE_TYPES.register(
            "blood",
            () -> new SimpleParticleType(true)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_SPLAT = PARTICLE_TYPES.register(
            "blood_splat",
            () -> new SimpleParticleType(true)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_FOG = PARTICLE_TYPES.register(
            "blood_fog",
            () -> new SimpleParticleType(true)
    );
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLOOD_SPLASH = PARTICLE_TYPES.register(
            "blood_splash",
            () -> new SimpleParticleType(true)
    );

    public ParticleTypes(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BLOOD.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(BLOOD_SPLAT.get(), BloodSplatParticle.Provider::new);
        event.registerSpriteSet(BLOOD_FOG.get(), BloodFogParticle.Provider::new);
        event.registerSpriteSet(BLOOD_SPLASH.get(), BloodSplashParticle.Provider::new);
    }
}
