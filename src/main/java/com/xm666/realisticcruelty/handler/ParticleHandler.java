package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.network.HitInfo;
import com.xm666.realisticcruelty.particle.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;

public class ParticleHandler {
    public static void gore(HitInfo hitInfo, float amount) {
        var fogSizeFactor = 0.5;
        var fogSizeMax = 1.5;
        var bloodSpeedFactor = 0.3;
        var bloodSpeedMax = 1.5;
        var amountSqrt = Math.sqrt(amount);
        var fogSize = Math.min(amountSqrt * fogSizeFactor, fogSizeMax);
        var bloodSpeed = Math.min(amountSqrt * bloodSpeedFactor, bloodSpeedMax);
        var hitPosition = hitInfo.getHitPosition();

        addParticle(ParticleTypes.BLOOD_FOG.get(), hitPosition, new Vec3(fogSize, 0.0, 0.0));
        while (amount >= 1 || amount > 0 && Random.nextFloat() < amount) {
            var speed = Random.nextDouble(bloodSpeed);
            var particleTransform = hitInfo.getParticleTransform();
            var position = particleTransform.position();
            var velocity = particleTransform.rotation().scale(speed);
            addParticle(ParticleTypes.BLOOD.get(), position, velocity);
            --amount;
        }
    }

    public static void addParticle(ParticleOptions particleData, Vec3 position, Vec3 velocity) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        level.addParticle(particleData, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
}
