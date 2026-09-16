package com.xm666.realisticcruelty.math;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record CollisionResult(Vec3 remainder, Direction normal) {
}
