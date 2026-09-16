package com.xm666.realisticcruelty.math;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class CollisionHandler {
    public static CollisionResult collideBoundingBox(@Nullable Entity entity, Vec3 velocity, AABB collisionBox, Level level, List<VoxelShape> potentialHits) {
        var colliders = collectColliders(entity, level, potentialHits, collisionBox.expandTowards(velocity));
        return collideWithShapes(velocity, collisionBox, colliders);
    }

    private static List<VoxelShape> collectColliders(@Nullable Entity entity, Level level, List<VoxelShape> collisions, AABB boundingBox) {
        var builder = ImmutableList.<VoxelShape>builderWithExpectedSize(collisions.size() + 1);
        if (!collisions.isEmpty()) {
            builder.addAll(collisions);
        }

        var worldBorder = level.getWorldBorder();
        var inside = entity != null && worldBorder.isInsideCloseToBorder(entity, boundingBox);
        if (inside) {
            builder.add(worldBorder.getCollisionShape());
        }

        builder.addAll(level.getBlockCollisions(entity, boundingBox));
        return builder.build();
    }

    private static CollisionResult collideWithShapes(Vec3 deltaMovement, AABB entityBB, List<VoxelShape> shapes) {
        if (shapes.isEmpty()) {
            return new CollisionResult(deltaMovement, null);
        } else {
            var xd = deltaMovement.x;
            var yd = deltaMovement.y;
            var zd = deltaMovement.z;
            var normal = (Direction) null;
            if (yd != 0.0) {
                yd = Shapes.collide(Direction.Axis.Y, entityBB, shapes, yd);
                if (yd != 0.0) {
                    entityBB = entityBB.move(0.0, yd, 0.0);
                    if (yd != deltaMovement.y)
                        normal = yd > 0.0 ? Direction.DOWN : Direction.UP;
                }
            }

            var flag = Math.abs(xd) < Math.abs(zd);
            if (flag && zd != 0.0) {
                zd = Shapes.collide(Direction.Axis.Z, entityBB, shapes, zd);
                if (zd != 0.0) {
                    entityBB = entityBB.move(0.0, 0.0, zd);
                    if (zd != deltaMovement.z)
                        normal = zd > 0.0 ? Direction.NORTH : Direction.SOUTH;
                }
            }

            if (xd != 0.0) {
                xd = Shapes.collide(Direction.Axis.X, entityBB, shapes, xd);
                if (!flag && xd != 0.0) {
                    entityBB = entityBB.move(xd, 0.0, 0.0);
                    if (xd != deltaMovement.x)
                        normal = xd > 0.0 ? Direction.WEST : Direction.EAST;
                }
            }

            if (!flag && zd != 0.0) {
                zd = Shapes.collide(Direction.Axis.Z, entityBB, shapes, zd);
                if (zd != deltaMovement.z)
                    normal = zd > 0.0 ? Direction.NORTH : Direction.SOUTH;
            }

            return new CollisionResult(new Vec3(xd, yd, zd), normal);
        }
    }
}
