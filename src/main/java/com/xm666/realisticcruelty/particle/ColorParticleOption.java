package com.xm666.realisticcruelty.particle;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.FastColor;

public class ColorParticleOption implements ParticleOptions {
    public static final ParticleOptions.Deserializer<ColorParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ColorParticleOption>() {
        public ColorParticleOption fromCommand(ParticleType<ColorParticleOption> p_123721_, StringReader p_123722_) throws CommandSyntaxException {
            return new ColorParticleOption(p_123721_, 0);
        }

        public ColorParticleOption fromNetwork(ParticleType<ColorParticleOption> p_123724_, FriendlyByteBuf p_123725_) {
            return new ColorParticleOption(p_123724_, p_123725_.readInt());
        }
    };
    private final ParticleType<ColorParticleOption> type;
    private final int color;

    private ColorParticleOption(ParticleType<ColorParticleOption> type, int color) {
        this.type = type;
        this.color = color;
    }

    public static Codec<ColorParticleOption> codec(ParticleType<ColorParticleOption> particleType) {
        return Codec.INT.xmap(p_333828_ -> new ColorParticleOption(particleType, p_333828_), p_333908_ -> p_333908_.color);
    }

    public static ColorParticleOption create(ParticleType<ColorParticleOption> type, int color) {
        return new ColorParticleOption(type, color);
    }

    public static ColorParticleOption create(ParticleType<ColorParticleOption> type, float red, float green, float blue) {
        return create(type, FastColor.ARGB32.color(255, (int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F)));
    }

    @Override
    public ParticleType<ColorParticleOption> getType() {
        return this.type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf p_123732_) {
        p_123732_.writeInt(this.color);
    }

    @Override
    public String writeToString() {
        return "";
    }

    public float getRed() {
        return (float) FastColor.ARGB32.red(this.color) / 255.0F;
    }

    public float getGreen() {
        return (float) FastColor.ARGB32.green(this.color) / 255.0F;
    }

    public float getBlue() {
        return (float) FastColor.ARGB32.blue(this.color) / 255.0F;
    }

    public float getAlpha() {
        return (float) FastColor.ARGB32.alpha(this.color) / 255.0F;
    }
}