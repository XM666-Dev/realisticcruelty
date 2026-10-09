package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.InverseFunction;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
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
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.List;

public class FragmentParticle extends ExtendedTextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    private static final InverseFunction FADE_OUT = new InverseFunction(0.8F, 0.8F, false);
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
    private final int startDuration;
    private final int endDuration;
    private final float rotAngleFrom;
    private final float rotAngleTo;
    private boolean stoppedByCollision;

    private FragmentParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(level, x, y, z);
        this.lifetime = 80;
        this.startDuration = 5;
        this.endDuration = 20;
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.quadSize = 0.2F;
        this.rotAngleFrom = Random.nextFloat(Mth.TWO_PI);
        this.rotAngleTo = getRotAngleTo();
        this.oRoll = this.rotAngleFrom;
        this.roll = this.rotAngleFrom;
        this.rCol = getRed(stack);
        this.gCol = getGreen(stack);
        this.bCol = getBlue(stack);
        this.setSprite(this.getSprites(stack));
        this.setParticleSpeed(xd, yd, zd);
    }

    private static float getRed(ItemStack stack) {
        return (float) FastColor.ARGB32.red(stack.getCount()) / 255.0F;
    }

    private static float getGreen(ItemStack stack) {
        return (float) FastColor.ARGB32.green(stack.getCount()) / 255.0F;
    }

    private static float getBlue(ItemStack stack) {
        return (float) FastColor.ARGB32.blue(stack.getCount()) / 255.0F;
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
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        var end = this.lifetime + 1 - this.endDuration;
        if (this.age >= end) return;

        var delta = FADE_IN.apply((float) this.age / end);
        this.roll = Mth.rotLerp(delta, this.rotAngleFrom, this.rotAngleTo);
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
                var remainder = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                x = remainder.x;
                y = remainder.y;
                z = remainder.z;
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

            if (this.onGround) {
                var end = this.lifetime + 1 - this.endDuration;
                if (this.age >= end) return;

                this.age = end;
                this.onCollided();
            }
        }
    }

    protected void onCollided() {
        this.yd = Random.nextDouble(0.1, 0.2);

        var fragmentSound = Config.FRAGMENT_SOUND.get();
        if (fragmentSound.isEmpty()) return;

        var fragmentVolumeMultiplier = Config.FRAGMENT_VOLUME_MULTIPLIER.get().floatValue();
        var sound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(fragmentSound));
        var volume = Random.nextFloat(0.3F, 1.0F) * fragmentVolumeMultiplier;
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
        y += 0.2F;
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ItemParticleOption> {
        public FragmentParticle createParticle(ItemParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var item = option.getItem();
            return new FragmentParticle(level, x, y, z, xd, yd, zd, item);
        }
    }
}
