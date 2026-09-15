package com.xm666.realisticcruelty.particle;


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

public class BloodParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);

    private BloodParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.1F;
        this.pickSprite(sprites);
        this.setParticleSpeed(xd, yd, zd);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            var end = this.lifetime + 1 - 10;
            if (this.age < end) {
                this.age = end;

                var enabled = true;
                var splashCount = 3;
                var volumeMultiplier = 1.0F;

                if (enabled) {
                    this.level.addParticle(ParticleTypes.BLOOD_SPLAT.get(), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
                }

                while (splashCount-- > 0) {
                    this.level.addParticle(ParticleTypes.BLOOD_SPLASH.get(), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
                }

                var sound = SoundEvents.BEEHIVE_DRIP;
                var volume = Random.nextFloat(0.3F, 1.0F) * volumeMultiplier;
                this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
            }
        }
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, 5, 10, this.lifetime + 1,
                (f) -> this.alpha = FADE_IN.apply(f),
                () -> this.alpha = 1.0F,
                (f) -> size[0] *= FADE_OUT.apply(f)
        );
        return size[0];
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public BloodParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new BloodParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}