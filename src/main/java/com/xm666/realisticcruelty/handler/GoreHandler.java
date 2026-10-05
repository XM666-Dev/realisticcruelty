package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.MixinConfig;
import com.xm666.realisticcruelty.RealisticCruelty;
import com.xm666.realisticcruelty.network.GorePayload;
import com.xm666.realisticcruelty.network.HitType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = RealisticCruelty.MODID)
public class GoreHandler {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (MixinConfig.CLIENT_GORE_ENABLED.get()) return;

        var target = event.getEntity();
        var source = event.getSource();
        var damage = event.getNewDamage();
        onDamage(target, source, damage);
    }

    public static void handlePayload(final GorePayload payload, final IPayloadContext context) {
        var hitType = HitType.values()[payload.hitType()];
        var targetBoundingBoxMin = new Vec3(payload.targetBoundingBoxMin());
        var targetBoundingBoxMax = new Vec3(payload.targetBoundingBoxMax());
        var targetBoundingBox = new AABB(targetBoundingBoxMin, targetBoundingBoxMax);
        var sourcePosition = new Vec3(payload.sourcePosition());
        var sourceDirection = new Vec3(payload.sourceDirection());
        var hitInfo = hitType.getHitInfo(targetBoundingBox, sourcePosition, sourceDirection);
        var amount = payload.amount();
        var color = payload.color();
        var item = payload.item();
        var itemType = BuiltInRegistries.ITEM.byId(item);
        ParticleHandler.gore(hitInfo, amount, color, itemType);
    }

    public static void onDamage(LivingEntity target, DamageSource source, float damage) {
        if (!isGoreEnabled(target)) return;

        var hitType = HitType.get(source);
        if (hitType == null) return;

        var amount = Math.min(damage, target.getMaxHealth());
        var item = getGoreItem(target);
        var color = getGoreColor(target, item);
        if (target.level().isClientSide()) {
            goreClient(hitType, target, source, amount, color, item);
            return;
        }

        gore(hitType, target, source, amount, color, item);
    }

    public static void gore(HitType hitType, LivingEntity target, DamageSource source, float amount, int color, int item) {
        var hitTypeOrdinal = hitType.ordinal();
        var targetBoundingBox = target.getBoundingBox();
        var targetBoundingBoxMin = targetBoundingBox.getMinPosition().toVector3f();
        var targetBoundingBoxMax = targetBoundingBox.getMaxPosition().toVector3f();
        var sourcePosition = hitType.getSourcePosition(source).toVector3f();
        var sourceDirection = hitType.getSourceDirection(source).toVector3f();
        var payload = new GorePayload(hitTypeOrdinal, targetBoundingBoxMin, targetBoundingBoxMax, sourcePosition, sourceDirection, amount, color, item);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, payload);
    }

    public static void goreClient(HitType hitType, LivingEntity target, DamageSource source, float amount, int color, int item) {
        var targetBoundingBox = target.getBoundingBox();
        var sourcePosition = hitType.getSourcePosition(source);
        var sourceDirection = hitType.getSourceDirection(source);
        var hitInfo = hitType.getHitInfo(targetBoundingBox, sourcePosition, sourceDirection);
        var itemType = BuiltInRegistries.ITEM.byId(item);
        ParticleHandler.gore(hitInfo, amount, color, itemType);
    }

    public static boolean isGoreEnabled(LivingEntity living) {
        var type = living.getType();
        return Config.GORE_USE_WHITELIST.get() == Config.goreBlacklist.contains(type);
    }

    public static int getGoreItem(LivingEntity living) {
        var type = living.getType();
        return Config.goreTextureItems.getOrDefault(type, 0);
    }

    public static int getGoreColor(LivingEntity living, int item) {
        var type = living.getType();
        return Config.goreColors.getOrDefault(type, item != 0 ? 0xffffff : Config.GORE_COLOR_DEFAULT.get());
    }
}
