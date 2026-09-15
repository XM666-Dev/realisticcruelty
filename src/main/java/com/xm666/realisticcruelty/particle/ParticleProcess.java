package com.xm666.realisticcruelty.particle;

import java.util.function.Consumer;

public class ParticleProcess {
    public static void apply(float tick, int initial, int end, int total, Consumer<Float> initialConsumer, Runnable middleRunnable, Consumer<Float> endConsumer) {
        if (tick < initial) {
            initialConsumer.accept(tick / initial);
            return;
        }

        var endStart = total - end;
        if (tick > endStart) {
            endConsumer.accept((tick - endStart) / end);
            return;
        }

        middleRunnable.run();
    }
}
