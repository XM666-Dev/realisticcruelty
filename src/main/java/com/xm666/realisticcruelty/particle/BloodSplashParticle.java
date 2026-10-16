package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BloodSplashParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.2F, 0.2F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private final int startDuration;
    private final int endDuration;
    private boolean stoppedByCollision;

    private BloodSplashParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        var velocity = this.getVelocity(xd, yd, zd);
        this.lifetime = 30;
        this.startDuration = 5;
        this.endDuration = 10;
        this.gravity = 1.0F;
        this.quadSize = 0.05F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
        this.setParticleSpeed(velocity.x, velocity.y, velocity.z);
    }

    private Vec3 getVelocity(double normalOrdinal, double pivotX, double pivotY) {
        var radiusHorizontal = 0.15F;
        var radiusMinY = 0.15;
        var radiusMaxY = 0.25;
        var x = Random.nextFloat(-radiusHorizontal, radiusHorizontal) + (float) pivotX * radiusHorizontal;
        var z = Random.nextFloat(-radiusHorizontal, radiusHorizontal) + (float) pivotY * radiusHorizontal;
        var y = Random.nextDouble(radiusMinY, radiusMaxY);
        var direction = Direction.values()[(int) normalOrdinal];
        var directionVector = new Vec3(direction.step());
        var componentVelocity = new Vector3f(x, z, 0.0F);
        var horizontalVelocity = new Vec3(componentVelocity.rotate(direction.getRotation().rotateX(Mth.HALF_PI)));
        var verticalVelocity = directionVector.scale(y);
        return horizontalVelocity.add(verticalVelocity);
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
                var travel = CollisionHandler.collideBoundingBox(new Vec3(x, y, z), this.getPos(), this.level).travel();
                x = travel.x;
                y = travel.y;
                z = travel.z;
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

            if (!this.onGround) return;

            var end = this.lifetime + 1 - this.endDuration;
            if (this.age >= end) return;

            this.age = end;
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
                    size[0] *= Mth.lerp(this.alpha, 2.0F, 1.0F);
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
            return new BloodSplashParticle(level, x, y, z, xd, yd, zd, r, g, b, sprites);
        }
    }
}
