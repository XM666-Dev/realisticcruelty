package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class BloodSplashParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.1F, 0.1F, false);
    private final int startDuration;
    private final int endDuration;

    private BloodSplashParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 20;
        this.startDuration = 5;
        this.endDuration = 10;
        this.gravity = 1.0F;
        this.quadSize = 0.05F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
        this.setParticleSpeed(Random.nextDouble(-0.1, 0.1), Random.nextDouble(0.2, 0.3), Random.nextDouble(-0.1, 0.1));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            var end = this.lifetime + 1 - this.endDuration;
            if (this.age < end) {
                this.age = end;
                var bloodSplashSound = Config.BLOOD_SPLASH_SOUND.get();
                if (bloodSplashSound.isEmpty()) return;

                var bloodSplashVolumeMultiplier = Config.BLOOD_SPLASH_VOLUME_MULTIPLIER.get().floatValue();
                var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(bloodSplashSound));
                var volume = Random.nextFloat(0.3F, 1.0F) * bloodSplashVolumeMultiplier;
                this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
            }
        }
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, this.startDuration, this.endDuration, this.lifetime + 1,
                (f) -> size[0] *= FADE_IN.apply(f),
                () -> {
                },
                (f) -> this.alpha = FADE_OUT.apply(f)
        );
        return size[0];
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodSplashParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodSplashParticle(level, x, y, z, r, g, b, sprites);
        }
    }
}
