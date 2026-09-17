package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.xm666.realisticcruelty.Config;
import com.xm666.realisticcruelty.math.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class FragmentParticle extends GoreParticle {
    private final float rotAngleFrom;
    private final float rotAngleTo;

    private FragmentParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ItemStack stack) {
        super(level, x, y, z, xd, yd, zd);
        this.quadSize = 0.2F;
        this.rotAngleFrom = Random.nextFloat(Mth.TWO_PI);
        this.rotAngleTo = getRotAngleTo();
        this.oRoll = this.rotAngleFrom;
        this.roll = this.rotAngleFrom;
        this.setSprite(this.getSprites(stack));
    }

    private float getRotAngleTo() {
        var rotAngle = Mth.DEG_TO_RAD * 3600.0F;
        return this.rotAngleFrom + Random.nextFloat(-rotAngle, rotAngle);
    }

    private TextureAtlasSprite getSprites(ItemStack stack) {
        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var model = itemRenderer.getModel(stack, level, null, 0);
        var bakedModel = model.getOverrides().resolve(model, stack, level, null, 0);
        return bakedModel.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        var end = this.lifetime + 1 - 10;
        if (this.age >= end) return;

        var delta = FADE_IN.apply((float) this.age / end);
        this.roll = Mth.lerp(delta, this.rotAngleFrom, this.rotAngleTo);
    }

    @Override
    protected void onCollided(Direction normal) {
        var fragmentSound = Config.FRAGMENT_SOUND.get();
        var fragmentVolumeMultiplier = Config.FRAGMENT_VOLUME_MULTIPLIER.get().floatValue();
        var sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(fragmentSound));
        var volume = Random.nextFloat(0.3F, 1.0F) * fragmentVolumeMultiplier;
        this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.BLOCKS, volume, 1.0F, false);
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = new float[]{this.quadSize};
        ParticleProcess.apply(tick, 5, 10, this.lifetime + 1,
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
        y += 0.1F;
        super.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ItemParticleOption> {
        public FragmentParticle createParticle(ItemParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var item = option.getItem();
            return new FragmentParticle(level, x, y, z, xd, yd, zd, item);
        }
    }
}
