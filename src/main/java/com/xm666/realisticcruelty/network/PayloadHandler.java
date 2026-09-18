package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.RealisticCruelty;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = RealisticCruelty.MODID)
public class PayloadHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RealisticCruelty.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        var index = 0;
        INSTANCE.registerMessage(index++, GorePayload.class, GorePayload::write, GorePayload::read, GorePayload::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
