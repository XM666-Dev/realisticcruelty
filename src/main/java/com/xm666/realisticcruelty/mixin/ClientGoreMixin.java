package com.xm666.realisticcruelty.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.realisticcruelty.ClientConfig;
import com.xm666.realisticcruelty.handler.ClientGoreHandler;
import com.xm666.realisticcruelty.handler.GoreHandler;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class ClientGoreMixin {
    @Mixin(ClientPacketListener.class)
    private static class ClientPacketListenerMixin {
        @WrapOperation(method = "handleDamageEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;handleDamageEvent(Lnet/minecraft/world/damagesource/DamageSource;)V"))
        private void onHandleDamageEvent(Entity instance, DamageSource damageSource, Operation<Void> original) {
            var clientBloodAmountMin = ClientConfig.CLIENT_BLOOD_AMOUNT_MIN.get().floatValue();
            var clientBloodAmountMax = ClientConfig.CLIENT_BLOOD_AMOUNT_MAX.get().floatValue();
            var amount = Random.nextFloat(clientBloodAmountMin, clientBloodAmountMax);
            original.call(instance, damageSource);
            GoreHandler.onDamage((LivingEntity) instance, damageSource, amount);
        }
    }

    @Mixin(ParticleEngine.class)
    private static class ParticleEngineMixin {
        @ModifyExpressionValue(method = "makeParticle", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
        private <T extends ParticleOptions> Object get(Object original, T particleOptions) {
            if (original != null) return original;

            return ClientGoreHandler.getProvider(particleOptions.getType());
        }
    }
}
