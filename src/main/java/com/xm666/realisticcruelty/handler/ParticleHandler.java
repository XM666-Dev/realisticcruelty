package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.network.HitInfo;
import com.xm666.realisticcruelty.particle.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class ParticleHandler {
    public static void gore(HitInfo hitInfo, float amount) {
        var bloodAmountMultiplier = Config.BLOOD_AMOUNT_MULTIPLIER.get().floatValue();
        var bloodAmountMax = Config.BLOOD_AMOUNT_MAX.get().floatValue();
        var bloodSpeedFactor = Config.BLOOD_SPEED_FACTOR.get();
        var bloodSpeedMax = Config.BLOOD_SPEED_MAX.get();
        var amountSqrt = Math.sqrt(amount);
        var bloodSpeed = Math.min(amountSqrt * bloodSpeedFactor, bloodSpeedMax);
        amount = Math.min(amount * bloodAmountMultiplier, bloodAmountMax);

        while (amount >= 1 || amount > 0 && Random.nextFloat() < amount) {
            var speed = Random.nextDouble(bloodSpeed);
            var transform = hitInfo.getParticleTransform();
            var position = transform.position();
            var velocity = transform.rotation().scale(speed);
            addParticle(ParticleTypes.BLOOD.get(), position, velocity);
            --amount;
        }

        var bloodFogSizeFactor = Config.BLOOD_FOG_SIZE_FACTOR.get();
        var bloodFogSizeMax = Config.BLOOD_FOG_SIZE_MAX.get();
        var hitPosition = hitInfo.getHitPosition();
        var bloodFogSize = Math.min(amountSqrt * bloodFogSizeFactor, bloodFogSizeMax);
        addParticle(ParticleTypes.BLOOD_FOG.get(), hitPosition, new Vec3(bloodFogSize, 0.0, 0.0));
    }

    public static void addParticle(ParticleOptions particleData, Vec3 position, Vec3 velocity) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        level.addParticle(particleData, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
}
