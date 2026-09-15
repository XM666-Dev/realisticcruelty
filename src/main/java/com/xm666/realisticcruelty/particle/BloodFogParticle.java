package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class BloodFogParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private final Quaternionf rotation;

    private BloodFogParticle(ClientLevel level, double x, double y, double z, double size, SpriteSet sprites) {
        super(level, x, y, z);
        var mc = Minecraft.getInstance();
        var gameRenderer = mc.gameRenderer;
        var camera = gameRenderer.getMainCamera();
        var rotation = new Quaternionf(camera.rotation()).rotateZ(Random.nextFloat(Mth.TWO_PI));
        this.lifetime = 10;
        this.rotation = rotation;
        this.quadSize = (float) size;
        this.pickSprite(sprites);
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = BloodFogParticle.FADE_IN.apply(tick / (this.lifetime + 1));
        this.alpha = size < 0.8F ? 1.0F : (1.0F - size) / 0.2F;
        return this.quadSize * size;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternionf, camera, v) -> quaternionf.set(rotation);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public BloodFogParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new BloodFogParticle(level, x, y, z, xd, sprites);
        }
    }
}
