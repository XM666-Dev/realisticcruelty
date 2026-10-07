package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BloodSplashParticle extends ExtendedTextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.2F, 0.2F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private final int startDuration;
    private final int endDuration;
    private boolean stoppedByCollision;

    private BloodSplashParticle(ClientLevel level, double x, double y, double z, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 30;
        this.startDuration = 5;
        this.endDuration = 10;
        this.gravity = 1.0F;
        this.quadSize = 0.05F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
        this.setParticleSpeed(Random.nextDouble(-0.15, 0.15), Random.nextDouble(0.15, 0.25), Random.nextDouble(-0.15, 0.15));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            var end = this.lifetime + 1 - this.endDuration;
            if (this.age < end) {
                this.age = end;
            }
        }
    }

    @Override
    public void move(double x, double y, double z) {
        if (!this.stoppedByCollision) {
            var xd = x;
            var yd = y;
            var zd = z;
            if (this.hasPhysics
                    && (x != 0.0 || y != 0.0 || z != 0.0)
                    && x * x + y * y + z * z < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                var result = CollisionHandler.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                var remainder = result.remainder();
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
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, this.startDuration, this.endDuration, this.lifetime + 1,
                (f) -> size[0] *= FADE_IN.apply(f),
                () -> {
                },
                (f) -> {
                    this.alpha = FADE_OUT.apply(f);
                    size[0] *= Mth.map(this.alpha, 1.0F, 0.0F, 1.0F, 2.0F);
                }
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
