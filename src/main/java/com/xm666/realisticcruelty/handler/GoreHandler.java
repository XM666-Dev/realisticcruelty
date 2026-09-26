package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.RealisticCruelty;
import com.xm666.realisticcruelty.math.VectorMath;
import com.xm666.realisticcruelty.network.GorePayload;
import com.xm666.realisticcruelty.network.HitType;
import com.xm666.realisticcruelty.network.PayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = RealisticCruelty.MODID)
public class GoreHandler {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        var targetEntity = event.getEntity();
        var level = targetEntity.level();
        if (level.isClientSide || !isGoreEnabled(targetEntity)) return;

        var source = event.getSource();
        var sourceEntity = source.getDirectEntity();
        if (sourceEntity == null) return;

        var hitType = HitType.get(source);
        var amount = event.getAmount();
        var item = getGoreItem(targetEntity);
        var color = getGoreColor(targetEntity, item);
        gore(hitType, targetEntity, sourceEntity, amount, color, item);
    }

    public static void handlePayload(final GorePayload payload, final Supplier<NetworkEvent.Context> context) {
        var hitType = HitType.values()[payload.hitType()];
        var targetBoundingBoxMin = new Vec3(payload.targetBoundingBoxMin());
        var targetBoundingBoxMax = new Vec3(payload.targetBoundingBoxMax());
        var targetBoundingBox = new AABB(targetBoundingBoxMin, targetBoundingBoxMax);
        var sourcePosition = new Vec3(payload.sourcePosition());
        var sourceDirection = new Vec3(payload.sourceDirection());
        var hitInfo = hitType.getHitInfo(targetBoundingBox, sourcePosition, sourceDirection);
        var amount = payload.amount();
        var color = payload.color();
        var itemId = payload.item();
        var item = BuiltInRegistries.ITEM.byId(itemId);
        ParticleHandler.gore(hitInfo, amount, color, item);
    }

    public static void gore(HitType hitType, LivingEntity target, Entity source, float amount, int color, int item) {
        var hitTypeOrdinal = hitType.ordinal();
        var targetBoundingBox = target.getBoundingBox();
        var targetBoundingBoxMin = VectorMath.getMinPosition(targetBoundingBox).toVector3f();
        var targetBoundingBoxMax = VectorMath.getMaxPosition(targetBoundingBox).toVector3f();
        var sourcePosition = hitType.getSourcePosition(source).toVector3f();
        var sourceDirection = hitType.getSourceDirection(source).toVector3f();
        var payload = new GorePayload(hitTypeOrdinal, targetBoundingBoxMin, targetBoundingBoxMax, sourcePosition, sourceDirection, amount, color, item);
        PayloadHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), payload);
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
