package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public enum HitType {
    GENERAL {
        @Override
        public Vec3 getSourcePosition(Entity entity) {
            return Vec3.ZERO;
        }

        @Override
        public Vec3 getSourceDirection(Entity entity) {
            return new Vec3(0.0, 1.0, 0.0);
        }

        @Override
        public HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection) {
            return new HitInfo.General(targetBoundingBox, sourcePosition, sourceDirection);
        }
    },
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
    private static final TagKey<DamageType> GORE_GENERAL = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(RealisticCruelty.MODID, "gore_general")
    );
    private static final TagKey<DamageType> GORE_PROJECTILE = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(RealisticCruelty.MODID, "gore_projectile")
    );
    private static final TagKey<DamageType> GORE_EXPLOSION = TagKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(RealisticCruelty.MODID, "gore_explosion")
    );

    public static HitType get(DamageSource source) {
        if (source.getDirectEntity() == null) {
            if (source.is(GORE_GENERAL)) {
                return GENERAL;
            }
            return null;
        }

        if (source.is(GORE_PROJECTILE)) {
            return PROJECTILE;
        } else if (source.is(GORE_EXPLOSION)) {
            return EXPLOSION;
        }
        return MELEE;
    }

    public abstract Vec3 getSourcePosition(Entity entity);

    public abstract Vec3 getSourceDirection(Entity entity);

    public abstract HitInfo getHitInfo(AABB targetBoundingBox, Vec3 sourcePosition, Vec3 sourceDirection);
}
