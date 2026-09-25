package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.ClipHandler;
import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.math.Transform;
import com.xm666.realisticcruelty.math.VectorMath;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class HitInfo {
    public static Vec3 addZOffset(Vec3 position, Vec3 direction) {
        var bloodZOffset = Config.BLOOD_Z_OFFSET.get();
        return position.add(direction.scale(bloodZOffset));
    }

    public abstract Vec3 getHitPosition();

    public abstract Transform getParticleTransform();

    public abstract double getParticleSpeedMinMultiplier();

    public abstract double getParticleSpeedMaxMultiplier();

    public abstract float getParticleSpreadDegrees();

    public abstract boolean isParticleFogEnabled();

    public abstract static class Ray extends HitInfo {
        private final Vec3 hitPosition;
        private final Vec3 particleDirection;

        public Ray(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            var destinationPosition = sourcePosition.add(sourceDirection);
            hitPosition = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition);
            particleDirection = sourceDirection.reverse();
        }

        @Override
        public Vec3 getHitPosition() {
            return addZOffset(hitPosition, particleDirection);
        }

        @Override
        public Transform getParticleTransform() {
            var spreadDegrees = getParticleSpreadDegrees();
            var rotationAngle = Random.nextAngle(spreadDegrees);
            var particleRotation = VectorMath.randomRotate(particleDirection, rotationAngle);
            return new Transform(hitPosition, particleRotation);
        }
    }

    public static class Melee extends Ray {
        public Melee(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            super(targetBoundingBox, sourcePosition, sourceDirection);
        }

        @Override
        public double getParticleSpeedMinMultiplier() {
            return Config.BLOOD_MELEE_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getParticleSpeedMaxMultiplier() {
            return Config.BLOOD_MELEE_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getParticleSpreadDegrees() {
            return Config.BLOOD_MELEE_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isParticleFogEnabled() {
            return Config.BLOOD_MELEE_FOG_ENABLED.get();
        }
    }

    public static class Projectile extends Ray {
        public Projectile(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            super(targetBoundingBox, sourcePosition, sourceDirection);
        }

        @Override
        public double getParticleSpeedMinMultiplier() {
            return Config.BLOOD_PROJECTILE_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getParticleSpeedMaxMultiplier() {
            return Config.BLOOD_PROJECTILE_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getParticleSpreadDegrees() {
            return Config.BLOOD_PROJECTILE_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isParticleFogEnabled() {
            return Config.BLOOD_PROJECTILE_FOG_ENABLED.get();
        }
    }

    public static class Explosion extends HitInfo {
        private final AABB targetBoundingBox;
        private final Vec3 sourcePosition;
        private final Vec3 hitPosition;
        private final Vec3 hitDirection;
        private final Vec3 particleDirection;

        public Explosion(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            this.targetBoundingBox = targetBoundingBox;
            this.sourcePosition = sourcePosition;
            hitPosition = VectorMath.clamp(sourcePosition, targetBoundingBox);
            hitDirection = getHitDirection();
            particleDirection = hitDirection.reverse();
        }

        private Vec3 getHitDirection() {
            if (!sourcePosition.equals(hitPosition)) return VectorMath.directionTo(sourcePosition, hitPosition);

            var targetCenter = targetBoundingBox.getCenter();
            if (!targetCenter.equals(sourcePosition)) return VectorMath.directionTo(sourcePosition, targetCenter);

            return new Vec3(0.0, -1.0, 0.0);
        }

        @Override
        public Vec3 getHitPosition() {
            return addZOffset(hitPosition, particleDirection);
        }

        @Override
        public Transform getParticleTransform() {
            var spreadDegrees = getParticleSpreadDegrees();
            var rotationAngle = Random.nextAngle(spreadDegrees);
            var hitRotation = VectorMath.randomRotate(hitDirection, rotationAngle);
            var destinationPosition = sourcePosition.add(hitRotation);
            var particlePosition = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition);
            var particleRotation = VectorMath.reflect(hitRotation, particleDirection);
            return new Transform(particlePosition, particleRotation);
        }

        @Override
        public double getParticleSpeedMinMultiplier() {
            return Config.BLOOD_EXPLOSION_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getParticleSpeedMaxMultiplier() {
            return Config.BLOOD_EXPLOSION_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getParticleSpreadDegrees() {
            return Config.BLOOD_EXPLOSION_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isParticleFogEnabled() {
            return Config.BLOOD_EXPLOSION_FOG_ENABLED.get();
        }
    }
}
