package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.VectorMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BloodSplatParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.9F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.9F, 0.2F, false);
    private final Direction rotation;

    private BloodSplatParticle(ClientLevel level, double x, double y, double z, double xd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = Config.BLOOD_SPLAT_LIFETIME.get();
        this.hasPhysics = false;
        this.quadSize = 0.5F;
        this.rotation = Direction.values()[(int) xd];
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
    }

    @Override
    public void move(double x, double y, double z) {
        var velocity = this.getDirectionVector().reverse();
        var movement = Entity.collideBoundingBox(null, velocity, this.getBoundingBox(), this.level, List.of());
        if (!VectorMath.abs(movement).equals(Vec3.ZERO)) {
            remove();
        }
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
        return (quaternionf, camera, v) -> quaternionf.set(this.getDirectionQuaternion());
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
        var distance = camera.getPosition().distanceTo(this.getPos());
        var offsetLength = Math.sqrt(distance) * 0.01;
        if (this.rotation == Direction.DOWN) {
            offsetLength -= this.bbHeight;
        } else if (this.rotation != Direction.UP) {
            offsetLength -= this.bbHeight * 0.5;
        }
        var offset = this.getDirectionVector().scale(offsetLength);
        x = (float) (x + offset.x);
        y = (float) (y + offset.y);
        z = (float) (z + offset.z);
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    private Vec3 getDirectionVector() {
        return new Vec3(this.rotation.step());
    }

    private Quaternionf getDirectionQuaternion() {
        return new Quaternionf().rotationTo(new Vector3f(0.0F, 0.0F, 1.0F), this.rotation.step());
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodSplatParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodSplatParticle(level, x, y, z, xd, r, g, b, sprites);
        }
    }
}
