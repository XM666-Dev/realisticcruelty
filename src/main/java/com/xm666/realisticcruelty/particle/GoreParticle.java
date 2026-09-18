package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.InverseFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GoreParticle extends ExtendedTextureSheetParticle {
    protected static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    protected static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private boolean stoppedByCollision;

    protected GoreParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.1F;
        this.setParticleSpeed(xd, yd, zd);
    }

    @Override
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
            var end = this.lifetime + 1 - 10;
            if (this.age >= end) return;

            this.age = end;
            this.stoppedByCollision = true;
            this.onCollided(normal);
        }
    }

    protected void onCollided(Direction normal) {
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
