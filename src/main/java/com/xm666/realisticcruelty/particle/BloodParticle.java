package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BloodParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private boolean stoppedByCollision;

    private BloodParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.1F;
        this.pickSprite(sprites);
        this.setParticleSpeed(xd, yd, zd);
    }

    public void move(double x, double y, double z) {
        var normal = (Direction) null;
        if (!this.stoppedByCollision) {
            var xd = x;
            var yd = y;
            var zd = z;
            if (this.hasPhysics
                    && (x != 0.0 || y != 0.0 || z != 0.0)
                    && x * x + y * y + z * z < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                var result = CollisionHandler.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                var remainder = result.remainder();
                normal = result.normal();
                x = remainder.x;
                y = remainder.y;
                z = remainder.z;
            }

            if (x != 0.0 || y != 0.0 || z != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(yd) >= 1.0E-5F && Math.abs(y) < 1.0E-5F) {
                this.stoppedByCollision = true;
            }

            this.onGround = yd != y && yd < 0.0;
            if (xd != x) {
                this.xd = 0.0;
            }

            if (zd != z) {
                this.zd = 0.0;
            }
        }
        if (normal != null) {
            collided(normal);
        }
    }

    public void collided(Direction normal) {
        var end = this.lifetime + 1 - 10;
        if (this.age < end) {
            this.age = end;
            this.stoppedByCollision = true;

            var bloodSplatEnabled = Config.BLOOD_SPLAT_ENABLED.get();
            var bloodSplashCount = Config.BLOOD_SPLASH_COUNT.get();
            var bloodVolume = Config.BLOOD_VOLUME.get().floatValue();

            if (bloodSplatEnabled) {
                this.level.addParticle(ParticleTypes.BLOOD_SPLAT.get(), this.x, this.y, this.z, normal.ordinal(), 0.0D, 0.0D);
            }

            while (bloodSplashCount-- > 0) {
                this.level.addParticle(ParticleTypes.BLOOD_SPLASH.get(), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            }

            var sound = SoundEvents.BEEHIVE_DRIP;
            var volume = Random.nextFloat(0.3F, 1.0F) * bloodVolume;
            this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
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
