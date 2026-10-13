package com.xm666.realisticcruelty.math;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class CollisionHandler {
    public static CollisionResult collideBoundingBox(Vec3 velocity, Vec3 from, Level level) {
        var to = from.add(velocity);
        var hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
        var collided = hitResult.getType() != HitResult.Type.MISS;
        var normal = hitResult.getDirection();
        var blockPos = hitResult.getBlockPos();
        if (collided) {
            var offset = velocity.scale(0.01 / velocity.length());
            var travel = hitResult.getLocation().subtract(from).subtract(offset);
            return new CollisionResult(true, travel, normal, blockPos);
        }

        return new CollisionResult(false, velocity, normal, blockPos);
    }
}
