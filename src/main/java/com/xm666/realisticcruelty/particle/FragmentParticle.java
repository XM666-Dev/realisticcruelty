package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.ClientConfig;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class FragmentParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.7F, 0.7F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private final int startDuration;
    private final int endDuration;
    private final float rotAngleFrom;
    private final float rotAngleTo;
    private boolean stoppedByCollision;

    private FragmentParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, ItemStack stack) {
        super(level, x, y, z);
        this.lifetime = 60;
        this.startDuration = 5;
        this.endDuration = 20;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.2F;
        this.rotAngleFrom = Random.nextFloat(Mth.TWO_PI);
        this.rotAngleTo = this.getRotAngleTo();
        this.oRoll = this.rotAngleFrom;
        this.roll = this.rotAngleFrom;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.setSprite(this.getSprites(stack));
        this.setParticleSpeed(xd, yd, zd);
    }

    private float getRotAngleTo() {
        var rotAngle = Mth.TWO_PI * 2.0F;
        return this.rotAngleFrom + Random.nextFloat(-rotAngle, rotAngle);
    }

    private TextureAtlasSprite getSprites(ItemStack stack) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var model = itemRenderer.getModel(stack, this.level, null, 0);
        var bakedModel = model.getOverrides().resolve(model, stack, this.level, null, 0);
        return bakedModel.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public void move(double x, double y, double z) {
        if (!this.stoppedByCollision) {
            var xd = x;
            var yd = y;
            var zd = z;
            if (this.hasPhysics
                    && (x != 0.0 || y != 0.0 || z != 0.0)
                    && x * x + y * y + z * z < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                var travel = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                x = travel.x;
                y = travel.y;
                z = travel.z;
            }

            if (x != 0.0 || y != 0.0 || z != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(yd) >= 1.0E-5 && Math.abs(y) < 1.0E-5) {
                this.stoppedByCollision = true;
            }

            this.onGround = yd != y && yd < 0.0;
            if (xd != x) {
                this.xd = 0.0;
            }

            if (zd != z) {
                this.zd = 0.0;
            }

            this.oRoll = this.roll;

            var end = this.lifetime + 1 - this.endDuration;
            if (this.age >= end) return;

            if (this.onGround) {
                this.age = end;
                this.onCollided();
                return;
            }

            var delta = FADE_IN.apply((float) this.age / end);
            this.roll = Mth.rotLerp(delta, this.rotAngleFrom, this.rotAngleTo);
        }
    }

    protected void onCollided() {
        this.xd *= 0.5;
        this.zd *= 0.5;
        this.yd = Random.nextDouble(0.1, 0.2);

        var fragmentSound = ClientConfig.FRAGMENT_SOUND.get();
        if (fragmentSound.isEmpty()) return;

        var fragmentVolumeMultiplierMin = ClientConfig.FRAGMENT_VOLUME_MULTIPLIER_MIN.get().floatValue();
        var fragmentVolumeMultiplierMax = ClientConfig.FRAGMENT_VOLUME_MULTIPLIER_MAX.get().floatValue();
        var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(fragmentSound));
        var volume = Random.nextFloat(fragmentVolumeMultiplierMin, fragmentVolumeMultiplierMax);
        this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, this.startDuration, this.endDuration, this.lifetime + 1,
                (f) -> size[0] *= FADE_IN.apply(f),
                () -> {
                },
                (f) -> this.alpha = FADE_OUT.apply(f)
        );
        return size[0];
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        var offset = this.getRenderOffset();
        x += offset.x;
        y += offset.y;
        z += offset.z;
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        var quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Camera camera, Quaternionf quaternion, float partialTicks) {
        var vec3 = camera.getPosition();
        var f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        var f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        var f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        this.renderRotatedQuad(buffer, quaternion, f, f1, f2, partialTicks);
    }

    private Vector3f getRenderOffset() {
        return new Vector3f(0.0F, 0.2F, 0.0F);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ItemParticleOption> {
        private static float getRed(int color) {
            return (float) FastColor.ARGB32.red(color) / 255.0F;
        }

        private static float getGreen(int color) {
            return (float) FastColor.ARGB32.green(color) / 255.0F;
        }

        private static float getBlue(int color) {
            return (float) FastColor.ARGB32.blue(color) / 255.0F;
        }

        public FragmentParticle createParticle(ItemParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var item = option.getItem();
            var color = item.getCount();
            var r = getRed(color);
            var g = getGreen(color);
            var b = getBlue(color);
            return new FragmentParticle(level, x, y, z, xd, yd, zd, r, g, b, item);
        }
    }
}
