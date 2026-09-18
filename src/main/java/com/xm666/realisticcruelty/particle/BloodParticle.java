package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class BloodParticle extends GoreParticle {
    private BloodParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
    }

    @Override
    protected void onCollided(Direction normal) {
        var bloodSplatEnabled = Config.BLOOD_SPLAT_ENABLED.get();
        if (bloodSplatEnabled) {
            var bloodSplat = ColorParticleOption.create(ParticleTypes.BLOOD_SPLAT.get(), this.rCol, this.gCol, this.bCol);
            this.level.addParticle(bloodSplat, this.x, this.y, this.z, normal.ordinal(), 0.0D, 0.0D);
        }

        var bloodSplashCountMin = Config.BLOOD_SPLASH_COUNT_MIN.get();
        var bloodSplashCountMax = Config.BLOOD_SPLASH_COUNT_MAX.get();
        var bloodSplashCount = Random.nextInt(bloodSplashCountMin, bloodSplashCountMax);
        var bloodSplash = ColorParticleOption.create(ParticleTypes.BLOOD_SPLASH.get(), this.rCol, this.gCol, this.bCol);
        while (bloodSplashCount-- > 0) {
            this.level.addParticle(bloodSplash, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
        }

        var bloodSound = Config.BLOOD_SOUND.get();
        var bloodVolumeMultiplier = Config.BLOOD_VOLUME_MULTIPLIER.get().floatValue();
        var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(bloodSound));
        var volume = Random.nextFloat(0.3F, 1.0F) * bloodVolumeMultiplier;
        this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
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

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodParticle(level, x, y, z, xd, yd, zd, r, g, b, sprites);
        }
    }
}
