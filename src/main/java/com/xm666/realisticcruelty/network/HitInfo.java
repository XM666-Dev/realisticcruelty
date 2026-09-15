package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.ClipHandler;
import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.math.Transform;
import com.xm666.realisticcruelty.math.VectorMath;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class HitInfo {
    public abstract Vec3 getHitPosition();

    public abstract Transform getParticleTransform();

    public static class Ray extends HitInfo {
        private final Vec3 hitPosition;
        private final Vec3 particleDirection;

        public Ray(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            var destinationPosition = sourcePosition.add(sourceDirection);
            var hitPoint = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition).orElse(sourcePosition);
            hitPosition = VectorMath.clamp(hitPoint, targetBoundingBox);
            particleDirection = sourceDirection.reverse();
        }

        @Override
        public Vec3 getHitPosition() {
            return hitPosition;
        }

        @Override
        public Transform getParticleTransform() {
            var bloodRaySpreadDegrees = Config.BLOOD_RAY_SPREAD_DEGREES.get().floatValue();
            var rotationAngle = Random.nextAngle(bloodRaySpreadDegrees);
            var particleRotation = VectorMath.randomRotate(particleDirection, rotationAngle);
            return new Transform(hitPosition, particleRotation);
        }
    }

    public static class Sphere extends HitInfo {
        private final AABB targetBoundingBox;
        private final Vec3 sourcePosition;
        private final Vec3 hitPosition;
        private final Vec3 hitDirection;
        private final Vec3 particleDirection;

        public Sphere(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            this.targetBoundingBox = targetBoundingBox;
            this.sourcePosition = sourcePosition;
            hitPosition = VectorMath.clamp(sourcePosition, targetBoundingBox);
            hitDirection = VectorMath.directionTo(sourcePosition, hitPosition);
            particleDirection = hitDirection.reverse();
        }

        @Override
        public Vec3 getHitPosition() {
            return hitPosition;
        }

        @Override
        public Transform getParticleTransform() {
            var bloodSphereSpreadDegrees = Config.BLOOD_SPHERE_SPREAD_DEGREES.get().floatValue();
            var rotationAngle = Random.nextAngle(bloodSphereSpreadDegrees);
            var hitRotation = VectorMath.randomRotate(hitDirection, rotationAngle);
            var destinationPosition = sourcePosition.add(hitRotation);
            var hitPoint = ClipHandler.expandedClip(targetBoundingBox, sourcePosition, destinationPosition).orElse(sourcePosition);
            var particlePosition = VectorMath.clamp(hitPoint, targetBoundingBox);
            var particleRotation = VectorMath.reflect(hitRotation, particleDirection);
            return new Transform(particlePosition, particleRotation);
        }
    }
}
