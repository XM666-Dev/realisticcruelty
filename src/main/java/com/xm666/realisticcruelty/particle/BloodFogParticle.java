package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.InverseFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;

public class BloodFogParticle extends TextureSheetParticle {
    private static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);

    private BloodFogParticle(ClientLevel level, double x, double y, double z, double xd, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.lifetime = 10;
        this.quadSize = (float) xd;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.pickSprite(sprites);
    }

    @Override
    public float getQuadSize(float partialTick) {
        var tick = this.age + partialTick;
        var size = FADE_IN.apply(tick / (this.lifetime + 1));
        this.alpha = Mth.clampedMap(size, 0.8F, 1.0F, 1.0F, 0.0F);
        return this.quadSize * size;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<ColorParticleOption> {
        public BloodFogParticle createParticle(ColorParticleOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            var r = option.getRed();
            var g = option.getGreen();
            var b = option.getBlue();
            return new BloodFogParticle(level, x, y, z, xd, r, g, b, sprites);
        }
    }
}
