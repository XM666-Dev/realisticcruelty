package com.xm666.realisticcruelty.handler;

import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.Random;
import com.xm666.realisticcruelty.network.HitInfo;
import com.xm666.realisticcruelty.particle.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ParticleHandler {
    public static void gore(HitInfo hitInfo, float amount, int color, Item item) {
        var amountSqrt = Math.sqrt(amount);
        var bloodSpeed = getBloodSpeed(amountSqrt);
        var bloodSpeedMin = getBloodSpeedMin(bloodSpeed, hitInfo);
        var bloodSpeedMax = getBloodSpeedMax(bloodSpeed, hitInfo);
        var bloodAmount = getBloodAmount(amount);
        var blood = getBlood(color, item);
        while (bloodAmount >= 1 || bloodAmount > 0 && Random.nextFloat() < bloodAmount) {
            var transform = hitInfo.getParticleTransform();
            var position = transform.position();
            var speed = Random.nextDouble(bloodSpeedMin, bloodSpeedMax);
            var velocity = transform.rotation().scale(speed);
            addParticle(blood, position, velocity);
            --bloodAmount;
        }
        if (!hitInfo.isParticleFogEnabled() || item != Items.AIR) return;

        var hitPosition = hitInfo.getHitPosition();
        var bloodFogSize = getBloodFogSize(amountSqrt);
        var bloodFog = ColorParticleOption.create(ParticleTypes.BLOOD_FOG.get(), color);
        addParticle(bloodFog, hitPosition, new Vec3(bloodFogSize, 0.0, 0.0));
    }

    public static ParticleOptions getBlood(int color, Item item) {
        if (item != Items.AIR) return new ItemParticleOption(ParticleTypes.FRAGMENT.get(), new ItemStack(item, color));

        return ColorParticleOption.create(ParticleTypes.BLOOD.get(), color);
    }

    public static float getBloodAmount(float amount) {
        var bloodAmountFactor = Config.BLOOD_AMOUNT_FACTOR.get().floatValue();
        var bloodAmountMax = Config.BLOOD_AMOUNT_MAX.get().floatValue();
        var bloodAmountAddition = Config.BLOOD_AMOUNT_ADDITION.get().floatValue();
        return Math.min(amount * bloodAmountFactor + bloodAmountAddition, bloodAmountMax);
    }

    public static double getBloodSpeed(double amountSqrt) {
        var bloodSpeedFactor = Config.BLOOD_SPEED_FACTOR.get();
        var bloodSpeedMax = Config.BLOOD_SPEED_MAX.get();
        return Math.min(amountSqrt * bloodSpeedFactor, bloodSpeedMax);
    }

    public static double getBloodSpeedMin(double bloodSpeed, HitInfo hitInfo) {
        return bloodSpeed * hitInfo.getParticleSpeedMinMultiplier();
    }

    public static double getBloodSpeedMax(double bloodSpeed, HitInfo hitInfo) {
        return bloodSpeed * hitInfo.getParticleSpeedMaxMultiplier();
    }

    public static double getBloodFogSize(double amountSqrt) {
        var bloodFogSizeFactor = Config.BLOOD_FOG_SIZE_FACTOR.get();
        var bloodFogSizeMax = Config.BLOOD_FOG_SIZE_MAX.get();
        return Math.min(amountSqrt * bloodFogSizeFactor, bloodFogSizeMax);
    }

    public static void addParticle(ParticleOptions particleData, Vec3 position, Vec3 velocity) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) return;

        level.addParticle(particleData, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
}
