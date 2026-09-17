package com.xm666.realisticcruelty.particle;

import com.mojang.serialization.MapCodec;
import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@Mod(RealisticCruelty.MODID)
@EventBusSubscriber(modid = RealisticCruelty.MODID)
public class ParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE,
            RealisticCruelty.MODID
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> BLOOD = PARTICLE_TYPES.register(
            "blood",
            ParticleTypes::createColorParticleType
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> BLOOD_SPLAT = PARTICLE_TYPES.register(
            "blood_splat",
            ParticleTypes::createColorParticleType
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> BLOOD_FOG = PARTICLE_TYPES.register(
            "blood_fog",
            ParticleTypes::createColorParticleType
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> BLOOD_SPLASH = PARTICLE_TYPES.register(
            "blood_splash",
            ParticleTypes::createColorParticleType
    );
    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemParticleOption>> FRAGMENT = PARTICLE_TYPES.register(
            "fragment",
            ParticleTypes::createItemParticleType
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
        event.registerSpriteSet(FRAGMENT.get(), FragmentParticle.Provider::new);
    }

    private static ParticleType<ColorParticleOption> createColorParticleType() {
        return createParticleType(true, ColorParticleOption::codec, ColorParticleOption::streamCodec);
    }

    private static ParticleType<ItemParticleOption> createItemParticleType() {
        return createParticleType(true, ItemParticleOption::codec, ItemParticleOption::streamCodec);
    }

    private static <T extends ParticleOptions> ParticleType<T> createParticleType(
            boolean overrideLimitter,
            final Function<ParticleType<T>, MapCodec<T>> codecGetter,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
    ) {
        return new ParticleType<>(overrideLimitter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        };
    }
}
