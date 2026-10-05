package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public enum HitType {
    GENERAL {
        @Override
        public boolean isSourceMatched(DamageSource source) {
            return source.is(GORE_GENERAL);
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
            return source.isDirect() && source.getDirectEntity() != null;
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
            return !source.isDirect() && source.getDirectEntity() != null;
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
            return source.is(GORE_EXPLOSION) && source.getSourcePosition() != null;
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
    private static final TagKey<DamageType> GORE_GENERAL = TagKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RealisticCruelty.MODID, "gore_general")
    );
    private static final TagKey<DamageType> GORE_EXPLOSION = TagKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RealisticCruelty.MODID, "gore_explosion")
    );

    public static HitType get(DamageSource source) {
        return Arrays.stream(HIT_TYPES).filter(type -> type.isSourceMatched(source)).findFirst().orElse(null);
    }

    public abstract boolean isSourceMatched(DamageSource source);

    public abstract Vec3 getSourcePosition(DamageSource source);

    public abstract Vec3 getSourceDirection(DamageSource source);

    public abstract HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection);
}
