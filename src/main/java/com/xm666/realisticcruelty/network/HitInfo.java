package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.ClipHandler;
import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.math.Transform;
import com.xm666.realisticcruelty.math.VectorMath;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class HitInfo {
    public static Vec3 addBloodDepthOffset(Vec3 position, Vec3 direction) {
        var bloodOffset = Config.BLOOD_DEPTH_OFFSET.get();
        return position.add(direction.scale(bloodOffset));
    }

    public static Vec3 addBloodFogDepthOffset(Vec3 position, Vec3 direction) {
        var bloodFogOffset = Config.BLOOD_FOG_DEPTH_OFFSET.get();
        return position.add(direction.scale(bloodFogOffset));
    }

    public abstract Vec3 getBloodFogPosition();

    public abstract Transform getBloodTransform();

    public abstract double getBloodSpeedMinMultiplier();

    public abstract double getBloodSpeedMaxMultiplier();

    public abstract float getBloodSpreadDegrees();

    public abstract boolean isBloodFogEnabled();

    public static class General extends HitInfo {
        private final Vec3 hitPosition;
        private final Vec3 particleDirection;

        public General(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            hitPosition = targetBoundingBox.getCenter();
            particleDirection = sourceDirection.reverse();
        }

        @Override
        public Vec3 getBloodFogPosition() {
            return addBloodFogDepthOffset(hitPosition, particleDirection);
        }

        @Override
        public Transform getBloodTransform() {
            var spreadDegrees = getBloodSpreadDegrees();
            var rotationAngle = Random.nextAngle(spreadDegrees);
            var particleRotation = VectorMath.randomRotate(particleDirection, rotationAngle);
            return new Transform(addBloodDepthOffset(hitPosition, particleRotation), particleRotation);
        }

        @Override
        public double getBloodSpeedMinMultiplier() {
            return Config.BLOOD_GENERAL_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getBloodSpeedMaxMultiplier() {
            return Config.BLOOD_GENERAL_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getBloodSpreadDegrees() {
            return Config.BLOOD_GENERAL_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isBloodFogEnabled() {
            return Config.BLOOD_GENERAL_FOG_ENABLED.get();
        }
    }

    public abstract static class Ray extends HitInfo {
        private final Vec3 hitPosition;
        private final Vec3 particleDirection;

        public Ray(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            var destinationPosition = sourcePosition.add(sourceDirection);
            hitPosition = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition);
            particleDirection = sourceDirection.reverse();
        }

        @Override
        public Vec3 getBloodFogPosition() {
            return addBloodFogDepthOffset(hitPosition, particleDirection);
        }

        @Override
        public Transform getBloodTransform() {
            var spreadDegrees = getBloodSpreadDegrees();
            var rotationAngle = Random.nextAngle(spreadDegrees);
            var particleRotation = VectorMath.randomRotate(particleDirection, rotationAngle);
            return new Transform(addBloodDepthOffset(hitPosition, particleRotation), particleRotation);
        }
    }

    public static class Melee extends Ray {
        public Melee(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            super(targetBoundingBox, sourcePosition, sourceDirection);
        }

        @Override
        public double getBloodSpeedMinMultiplier() {
            return Config.BLOOD_MELEE_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getBloodSpeedMaxMultiplier() {
            return Config.BLOOD_MELEE_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getBloodSpreadDegrees() {
            return Config.BLOOD_MELEE_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isBloodFogEnabled() {
            return Config.BLOOD_MELEE_FOG_ENABLED.get();
        }
    }

    public static class Projectile extends Ray {
        public Projectile(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            super(targetBoundingBox, sourcePosition, sourceDirection);
        }

        @Override
        public double getBloodSpeedMinMultiplier() {
            return Config.BLOOD_PROJECTILE_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getBloodSpeedMaxMultiplier() {
            return Config.BLOOD_PROJECTILE_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getBloodSpreadDegrees() {
            return Config.BLOOD_PROJECTILE_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isBloodFogEnabled() {
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
        public Vec3 getBloodFogPosition() {
            return addBloodFogDepthOffset(hitPosition, particleDirection);
        }

        @Override
        public Transform getBloodTransform() {
            var spreadDegrees = getBloodSpreadDegrees();
            var rotationAngle = Random.nextAngle(spreadDegrees);
            var hitRotation = VectorMath.randomRotate(hitDirection, rotationAngle);
            var destinationPosition = sourcePosition.add(hitRotation);
            var particlePosition = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition);
            var particleRotation = VectorMath.reflect(hitRotation, particleDirection);
            return new Transform(addBloodDepthOffset(particlePosition, particleRotation), particleRotation);
        }

        @Override
        public double getBloodSpeedMinMultiplier() {
            return Config.BLOOD_EXPLOSION_SPEED_MIN_MULTIPLIER.get();
        }

        @Override
        public double getBloodSpeedMaxMultiplier() {
            return Config.BLOOD_EXPLOSION_SPEED_MAX_MULTIPLIER.get();
        }

        @Override
        public float getBloodSpreadDegrees() {
            return Config.BLOOD_EXPLOSION_SPREAD_DEGREES.get().floatValue();
        }

        @Override
        public boolean isBloodFogEnabled() {
            return Config.BLOOD_EXPLOSION_FOG_ENABLED.get();
        }
    }
}
