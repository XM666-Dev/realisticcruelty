package com.xm666.realisticcruelty.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record CollisionResult(boolean collided, Vec3 travel, Direction normal, BlockPos blockPos) {
}
