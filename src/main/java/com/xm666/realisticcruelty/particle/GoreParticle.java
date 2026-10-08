package com.xm666.realisticcruelty.particle;

import com.xm666.realisticcruelty.math.InverseFunction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;

public class GoreParticle extends ExtendedTextureSheetParticle {
    protected static final InverseFunction FADE_IN = new InverseFunction(0.2F, 0.8F, true);
    protected static final InverseFunction FADE_OUT = new InverseFunction(0.2F, 0.2F, false);
    protected final int startDuration;
    protected final int endDuration;

    protected GoreParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, float r, float g, float b, int startDuration, int endDuration) {
        super(level, x, y, z);
        this.gravity = 1.5F;
        this.friction = 0.95F;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.startDuration = startDuration;
        this.endDuration = endDuration;
        this.setParticleSpeed(xd, yd, zd);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
