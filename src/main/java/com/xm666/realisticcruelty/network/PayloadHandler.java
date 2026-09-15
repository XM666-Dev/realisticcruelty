package com.xm666.realisticcruelty.network;

import com.xm666.realisticcruelty.RealisticCruelty;
import com.xm666.realisticcruelty.handler.GoreHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = RealisticCruelty.MODID)
public class PayloadHandler {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(
                GorePayload.TYPE,
                GorePayload.STREAM_CODEC,
                GoreHandler::handlePayload
        );
    }
}
