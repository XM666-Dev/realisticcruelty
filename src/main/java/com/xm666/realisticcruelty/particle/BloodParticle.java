package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.CollisionResult;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BloodParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.2F, 0.2F, false);
    private static final double BUOYANCY = 1.0;
    private static final double FLUID_RESISTANCE = 0.95;
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private final int startDuration;
    private final int endDuration;
    private boolean stoppedByCollision;

    private BloodParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.startDuration = 5;
        this.endDuration = 10;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.1F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
        this.setParticleSpeed(xd, yd, zd);
    }

    @Override
    public void move(double x, double y, double z) {
        if (!this.stoppedByCollision) {
            var inFluid = this.isInFluid();
            if (inFluid) {
                this.yd += 0.04 * BUOYANCY;
                this.xd *= FLUID_RESISTANCE;
                this.yd *= FLUID_RESISTANCE;
                this.zd *= FLUID_RESISTANCE;
            }
            var result = (CollisionResult) null;
            var xd = x;
            var yd = y;
            var zd = z;
            if (this.hasPhysics
                    && (x != 0.0 || y != 0.0 || z != 0.0)
                    && x * x + y * y + z * z < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                result = CollisionHandler.collideBoundingBox(new Vec3(x, y, z), this.getPos(), this.level);
                var remainder = result.remainder();
                x = remainder.x;
                y = remainder.y;
                z = remainder.z;
            }

            if (x != 0.0 || y != 0.0 || z != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(yd) >= 1.0E-5 && Math.abs(y) < 1.0E-5) {
                this.stoppedByCollision = true;
            }

            this.onGround = yd != y && yd < 0.0;
            if (xd != x) {
                this.xd = 0.0;
            }

            if (zd != z) {
                this.zd = 0.0;
            }

            if (result != null && result.collided()) {
                var end = this.lifetime + 1 - this.endDuration;
                if (this.age >= end) return;

                this.age = end;
                if (inFluid) return;

                this.onCollided(result.normal());
            }
            if (inFluid && this.age > 20) {
                var end = this.lifetime + 1 - this.endDuration;
                if (this.age >= end) return;

                this.age = end;
            }
        }
    }

    protected void onCollided(Direction normal) {
        this.stoppedByCollision = true;

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
        if (bloodSound.isEmpty()) return;

        var bloodVolumeMultiplier = Config.BLOOD_VOLUME_MULTIPLIER.get().floatValue();
        var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(bloodSound));
        var volume = Random.nextFloat(0.3F, 1.0F) * bloodVolumeMultiplier;
        this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
    }

    private boolean isInFluid() {
        var pos = this.getPos();
        var blockPos = BlockPos.containing(pos);
        var fluidState = this.level.getFluidState(blockPos);
        if (fluidState.isEmpty()) return false;

        var fluidY = blockPos.getY();
        var fluidHeight = fluidState.getHeight(this.level, blockPos);
        return fluidY + fluidHeight > this.y;
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, this.startDuration, this.endDuration, this.lifetime + 1,
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

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodParticle(level, x, y, z, xd, yd, zd, r, g, b, sprites);
        }
    }
}
