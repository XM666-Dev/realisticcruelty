package com.xm666.realisticcruelty.particle;

import com.mojang.serialization.Codec;
import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = RealisticCruelty.MODID)
public class ParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            ForgeRegistries.PARTICLE_TYPES,
            RealisticCruelty.MODID
    );
    public static final RegistryObject<ParticleType<ColorParticleOption>> BLOOD = PARTICLE_TYPES.register(
            "blood",
            ParticleTypes::createColorParticleType
    );
    public static final RegistryObject<ParticleType<ColorParticleOption>> BLOOD_SPLAT = PARTICLE_TYPES.register(
            "blood_splat",
            ParticleTypes::createColorParticleType
    );
    public static final RegistryObject<ParticleType<ColorParticleOption>> BLOOD_FOG = PARTICLE_TYPES.register(
            "blood_fog",
            ParticleTypes::createColorParticleType
    );
    public static final RegistryObject<ParticleType<ColorParticleOption>> BLOOD_SPLASH = PARTICLE_TYPES.register(
            "blood_splash",
            ParticleTypes::createColorParticleType
    );
    public static final RegistryObject<ParticleType<ItemParticleOption>> FRAGMENT = PARTICLE_TYPES.register(
            "fragment",
            ParticleTypes::createItemParticleType
    );

    public static void init(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
        if (FMLEnvironment.dist != Dist.CLIENT) return;

        modEventBus.addListener(ParticleTypes::registerParticleProviders);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BLOOD.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(BLOOD_SPLAT.get(), BloodSplatParticle.Provider::new);
        event.registerSpriteSet(BLOOD_FOG.get(), BloodFogParticle.Provider::new);
        event.registerSpriteSet(BLOOD_SPLASH.get(), BloodSplashParticle.Provider::new);
        event.registerSpriteSet(FRAGMENT.get(), FragmentParticle.Provider::new);
    }

    private static ParticleType<ColorParticleOption> createColorParticleType() {
        return createParticleType(true, ColorParticleOption.DESERIALIZER, ColorParticleOption::codec);
    }

    private static ParticleType<ItemParticleOption> createItemParticleType() {
        return createParticleType(true, ItemParticleOption.DESERIALIZER, ItemParticleOption::codec);
    }

    private static <T extends ParticleOptions> ParticleType<T> createParticleType(boolean p_235907_, ParticleOptions.Deserializer<T> p_235908_, final Function<ParticleType<T>, Codec<T>> p_235909_) {
        return new ParticleType<>(p_235907_, p_235908_) {
            public Codec<T> codec() {
                return p_235909_.apply(this);
            }
        };
    }
}
