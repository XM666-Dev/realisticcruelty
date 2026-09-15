package com.xm666.realisticcruelty.particle;


import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.math.InverseFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class BloodSplatParticle extends TextureSheetParticle {
    private static final Quaternionf UP = new Quaternionf().rotateX(-Mth.HALF_PI);
    private static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);

    private BloodSplatParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 100;
        this.quadSize = 0.5F;
        this.pickSprite(sprites);
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        ParticleProcess.apply(tick, 10, 20, this.lifetime + 1,
                (f) -> this.alpha = FADE_IN.apply(f),
                () -> this.alpha = 1.0F,
                (f) -> this.alpha = FADE_OUT.apply(f)
        );
        return this.quadSize * this.alpha;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternionf, camera, v) -> quaternionf.set(UP);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        var mc = Minecraft.getInstance();
        var gameRenderer = mc.gameRenderer;
        var camera = gameRenderer.getMainCamera();
        var cameraPosition = camera.getPosition();
        var distance = cameraPosition.distanceTo(getPos());
        var offset = 0.001 * Math.sqrt(distance);
        y = (float) (y + offset);
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        public BloodSplatParticle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new BloodSplatParticle(level, x, y, z, sprites);
        }
    }
}
