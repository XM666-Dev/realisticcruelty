package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.handler.TagObject;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public enum HitType {
    GENERAL {
        @Override
        public boolean isSourceMatched(DamageSource source) {
            return is(source, Config.goreGeneralTagObject);
        }

        @Override
        public Vec3 getSourcePosition(DamageSource source) {
            return Vec3.ZERO;
        }

        @Override
        public Vec3 getSourceDirection(DamageSource source) {
            return new Vec3(0.0, 1.0, 0.0);
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.General(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
    MELEE {
        @Override
        public boolean isSourceMatched(DamageSource source) {
            return !source.isIndirect() && source.getDirectEntity() != null;
        }

        @Override
        public Vec3 getSourcePosition(DamageSource source) {
            return source.getDirectEntity().getEyePosition();
        }

        @Override
        public Vec3 getSourceDirection(DamageSource source) {
            return source.getDirectEntity().getLookAngle();
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Melee(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
    PROJECTILE {
        @Override
        public boolean isSourceMatched(DamageSource source) {
            return source.isIndirect() && source.getDirectEntity() != null;
        }

        @Override
        public Vec3 getSourcePosition(DamageSource source) {
            return source.getSourcePosition();
        }

        @Override
        public Vec3 getSourceDirection(DamageSource source) {
            return source.getDirectEntity().getDeltaMovement().normalize();
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Projectile(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
    EXPLOSION {
        @Override
        public boolean isSourceMatched(DamageSource source) {
            return is(source, Config.goreExplosionTagObject) && source.getSourcePosition() != null;
        }

        public Vec3 getSourcePosition(DamageSource source) {
            return source.getSourcePosition();
        }

        public Vec3 getSourceDirection(DamageSource source) {
            return Vec3.ZERO;
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.Explosion(targetBoundingBox, sourcePosition, sourceDirection);
        }
    };
    private static final HitType[] HIT_TYPES = new HitType[]{EXPLOSION, MELEE, PROJECTILE, GENERAL};

    public static HitType get(DamageSource source) {
        return Arrays.stream(HIT_TYPES).filter(type -> type.isSourceMatched(source)).findFirst().orElse(null);
    }

    public static boolean is(DamageSource source, TagObject<DamageType> tagObject) {
        return tagObject.is(source.typeHolder());
    }

    public abstract boolean isSourceMatched(DamageSource source);

    public abstract Vec3 getSourcePosition(DamageSource source);

    public abstract Vec3 getSourceDirection(DamageSource source);

    public abstract HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection);
}
