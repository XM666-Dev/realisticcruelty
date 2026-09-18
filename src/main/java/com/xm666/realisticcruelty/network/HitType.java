package com.xm666.realisticcruelty.network;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public enum HitType {
    MELEE {
        @Override
        public Vec3 getSourcePosition(Entity entity) {
            return entity.getEyePosition();
        }

        @Override
        public Vec3 getSourceDirection(Entity entity) {
            return entity.getLookAngle();
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Melee(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
    PROJECTILE {
        @Override
        public Vec3 getSourcePosition(Entity entity) {
            return entity.position();
        }

        @Override
        public Vec3 getSourceDirection(Entity entity) {
            return entity.getDeltaMovement().normalize();
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Projectile(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
    EXPLOSION {
        public Vec3 getSourcePosition(Entity entity) {
            return entity.position();
        }

        public Vec3 getSourceDirection(Entity entity) {
            return Vec3.ZERO;
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Explosion(targetBoundingBox, sourcePosition, sourceDirection);
        }
    };

    public static HitType get(DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return HitType.EXPLOSION;
        } else if (!damageSource.isIndirect()) {
            return HitType.MELEE;
        }
        return HitType.PROJECTILE;
    }

    public abstract Vec3 getSourcePosition(Entity entity);

    public abstract Vec3 getSourceDirection(Entity entity);

    public abstract HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection);
}
