package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.handler.GoreHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector3f;

import java.util.function.Supplier;

public record GorePayload(
        int hitType,
        Vector3f targetBoundingBoxMin,
        Vector3f targetBoundingBoxMax,
        Vector3f sourcePosition,
        Vector3f sourceDirection,
        float amount,
        int color,
        int item
) {
    public static void write(GorePayload msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.hitType);
        buf.writeVector3f(msg.targetBoundingBoxMin);
        buf.writeVector3f(msg.targetBoundingBoxMax);
        buf.writeVector3f(msg.sourcePosition);
        buf.writeVector3f(msg.sourceDirection);
        buf.writeFloat(msg.amount);
        buf.writeInt(msg.color);
        buf.writeInt(msg.item);
    }

    public static GorePayload read(FriendlyByteBuf buf) {
        return new GorePayload(
                buf.readInt(),
                buf.readVector3f(),
                buf.readVector3f(),
                buf.readVector3f(),
                buf.readVector3f(),
                buf.readFloat(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(GorePayload msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GoreHandler.handlePayload(msg, ctx);
        });
        ctx.get().setPacketHandled(true);
    }
}
