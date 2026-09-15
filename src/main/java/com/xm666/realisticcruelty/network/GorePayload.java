package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.RealisticCruelty;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public record GorePayload(
        int hitType,
        Vector3f targetBoundingBoxMin,
        Vector3f targetBoundingBoxMax,
        Vector3f sourcePosition,
        Vector3f sourceDirection,
        float amount
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GorePayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(RealisticCruelty.MODID, "gore")
    );
    public static final StreamCodec<ByteBuf, GorePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            GorePayload::hitType,
            ByteBufCodecs.VECTOR3F,
            GorePayload::targetBoundingBoxMin,
            ByteBufCodecs.VECTOR3F,
            GorePayload::targetBoundingBoxMax,
            ByteBufCodecs.VECTOR3F,
            GorePayload::sourcePosition,
            ByteBufCodecs.VECTOR3F,
            GorePayload::sourceDirection,
            ByteBufCodecs.FLOAT,
            GorePayload::amount,
            GorePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
