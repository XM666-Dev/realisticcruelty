package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.RealisticCruelty;
import com.xm666.realisticcruelty.math.VectorMath;
import com.xm666.realisticcruelty.network.GorePayload;
import com.xm666.realisticcruelty.network.HitType;
import com.xm666.realisticcruelty.network.PayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
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
        var color = getGoreColor(targetEntity);
        var item = getGoreItem(targetEntity);
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

    public static void gore(HitType hitType, LivingEntity target, Entity source, float amount, int color, ResourceLocation item) {
        var hitTypeOrdinal = hitType.ordinal();
        var targetBoundingBox = target.getBoundingBox();
        var targetBoundingBoxMin = VectorMath.getMinPosition(targetBoundingBox).toVector3f();
        var targetBoundingBoxMax = VectorMath.getMaxPosition(targetBoundingBox).toVector3f();
        var sourcePosition = hitType.getSourcePosition(source).toVector3f();
        var sourceDirection = hitType.getSourceDirection(source).toVector3f();
        var itemId = BuiltInRegistries.ITEM.getId(BuiltInRegistries.ITEM.get(item));
        var payload = new GorePayload(hitTypeOrdinal, targetBoundingBoxMin, targetBoundingBoxMax, sourcePosition, sourceDirection, amount, color, itemId);
        PayloadHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), payload);
    }

    public static boolean isGoreEnabled(LivingEntity living) {
        var type = living.getType();
        var livingEntity = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (Config.GORE_USE_WHITELIST.get()) {
            for (var path : Config.GORE_BLACKLIST.get()) {
                var entity = ResourceLocation.parse(path);
                if (livingEntity.equals(entity)) return true;
            }
            return false;
        }

        for (var path : Config.GORE_BLACKLIST.get()) {
            var entity = ResourceLocation.parse(path);
            if (livingEntity.equals(entity)) return false;
        }
        return true;
    }

    public static int getGoreColor(LivingEntity living) {
        var type = living.getType();
        var livingEntity = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        for (var pair : Config.GORE_COLORS.get()) {
            var strings = Config.splitPair(pair);
            var entity = ResourceLocation.parse(strings[0]);
            var color = Integer.decode(strings[1]);
            if (livingEntity.equals(entity)) return color;
        }

        return Config.GORE_COLOR_DEFAULT.get();
    }

    public static ResourceLocation getGoreItem(LivingEntity living) {
        var type = living.getType();
        var livingEntity = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        for (var pair : Config.GORE_TEXTURE_ITEMS.get()) {
            var strings = Config.splitPair(pair);
            var entity = ResourceLocation.parse(strings[0]);
            var item = ResourceLocation.parse(strings[1]);
            if (livingEntity.equals(entity)) return item;
        }

        return BuiltInRegistries.ITEM.getKey(Items.AIR);
    }
}
