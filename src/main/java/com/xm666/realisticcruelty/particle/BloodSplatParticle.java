package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.ClientConfig;
import com.xm666.realisticcruelty.math.CollisionHandler;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BloodSplatParticle extends ExtendedTextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.2F, 0.6F, false);
    private final int startDuration;
    private final int endDuration;
    private final Direction direction;
    private final float pivotX;
    private final float pivotY;
    private final BlockPos attachedPos;
    private final VoxelShape attachedShape;

    private BloodSplatParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        var spriteIndex = Random.nextInt(0, 11);
        var spriteScale = this.getSpriteScale(spriteIndex);
        this.lifetime = ClientConfig.BLOOD_SPLAT_LIFETIME.get();
        this.startDuration = 10;
        this.endDuration = 40;
        this.quadSize = 0.5F;
        this.direction = Direction.values()[(int) xd];
        this.pivotX = (float) yd * spriteScale;
        this.pivotY = (float) zd * spriteScale;
        this.attachedPos = this.getAttachedPos();
        this.attachedShape = this.getAttachedShape();
        this.roll = Random.nextInt(3) * Mth.HALF_PI;
        this.oRoll = this.roll;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.setSprite(sprites.get(spriteIndex, 11));
    }

    private BlockPos getAttachedPos() {
        var velocity = this.getDirectionVector().scale(-0.01);
        var result = CollisionHandler.collideBoundingBox(velocity, this.getPos(), this.level);
        return result.blockPos();
    }

    private VoxelShape getAttachedShape() {
        var state = this.level.getBlockState(this.attachedPos);
        return state.getVisualShape(this.level, this.attachedPos, CollisionContext.empty());
    }

    private float getSpriteScale(int spriteIndex) {
        if (spriteIndex < 4) {
            return 1.0F;
        } else if (spriteIndex < 8) {
            return 0.75F;
        } else {
            return 0.5F;
        }
    }

    @Override
    public void move(double x, double y, double z) {
        if (this.getAttachedShape() == this.attachedShape) return;

        this.remove();
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        ParticleProcess.apply(tick, this.startDuration, this.endDuration, this.lifetime + 1,
                (f) -> this.alpha = FADE_IN.apply(f),
                () -> this.alpha = 1.0F,
                (f) -> this.alpha = FADE_OUT.apply(f)
        );
        return this.quadSize * this.alpha;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternionf, camera, v) -> quaternionf.set(this.getRotation());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        var offset = this.getRenderOffset(partialTicks);
        x += offset.x;
        y += offset.y;
        z += offset.z;
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    @Override
    public AABB getBoundingBox() {
        return super.getBoundingBox().inflate(1.0);
    }

    private Vector3f getRenderOffset(float partialTick) {
        var mc = Minecraft.getInstance();
        var gameRenderer = mc.gameRenderer;
        var camera = gameRenderer.getMainCamera();
        var distance = camera.getPosition().distanceTo(this.getPos());
        var offsetLength = Math.sqrt(distance) * 0.01;
        var offset = this.getDirectionVector().scale(offsetLength);
        var size = this.getQuadSize(partialTick);
        var pivot = new Vector3f(this.pivotX * size, this.pivotY * size, 0.0F);
        var pivotOffset = pivot.rotate(this.getPivotRotation());
        return offset.toVector3f().add(pivotOffset);
    }

    private Vec3 getDirectionVector() {
        return new Vec3(this.direction.step());
    }

    private Quaternionf getRotation() {
        return this.direction.getRotation().rotateX(Mth.HALF_PI);
    }

    private Quaternionf getPivotRotation() {
        return this.direction.getRotation().rotateX(Mth.HALF_PI);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodSplatParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodSplatParticle(level, x, y, z, xd, yd, zd, r, g, b, sprites);
        }
    }
}
