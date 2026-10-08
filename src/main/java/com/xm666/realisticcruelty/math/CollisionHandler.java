package com.xm666.realisticcruelty.math;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class CollisionHandler {
    public static CollisionResult collideBoundingBox(Vec3 velocity, Vec3 from, Level level) {
        var to = from.add(velocity);
        var hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty()));
        var collided = hitResult.getType() != HitResult.Type.MISS;
        if (collided) {
            var offset = velocity.scale(0.001 / velocity.length());
            var remainder = hitResult.getLocation().subtract(from).subtract(offset);
            var normal = hitResult.getDirection();
            return new CollisionResult(true, remainder, normal);
        }

        return new CollisionResult(false, velocity, null);
    }
}
