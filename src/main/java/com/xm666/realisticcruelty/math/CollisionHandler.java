package com.xm666.realisticcruelty.math;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CollisionHandler {
    public static CollisionResult collideBoundingBox(Vec3 velocity, Vec3 from, Level level) {
        var to = from.add(velocity);
        var hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, null));
        var collided = hitResult.getType() != HitResult.Type.MISS;
        if (collided)
            return new CollisionResult(true, hitResult.getLocation().subtract(from), hitResult.getDirection());

        return new CollisionResult(false, velocity, null);
    }
}
