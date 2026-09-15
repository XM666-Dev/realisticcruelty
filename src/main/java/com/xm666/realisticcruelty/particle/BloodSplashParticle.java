package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class BloodSplashParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);

    private BloodSplashParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 20;
        this.gravity = 1.0F;
        this.quadSize = 0.05F;
        this.pickSprite(sprites);
        this.setParticleSpeed(Random.nextDouble(-0.15, 0.15), Random.nextDouble(0.1, 0.3), Random.nextDouble(-0.15, 0.15));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            var end = this.lifetime + 1 - 5;
            if (this.age < end) {
                this.age = end;

                var bloodSplashVolume = Config.BLOOD_SPLASH_VOLUME.get().floatValue();
                var sound = SoundEvents.BEEHIVE_DRIP;
                var volume = Mth.randomBetween(this.random, 0.3F, 1.0F) * bloodSplashVolume;
                this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
            }
        }
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, 5, 5, this.lifetime + 1,
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

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public BloodSplashParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new BloodSplashParticle(level, x, y, z, sprites);
        }
    }
}
