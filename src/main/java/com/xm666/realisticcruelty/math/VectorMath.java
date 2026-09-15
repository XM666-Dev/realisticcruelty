package com.xm666.realisticcruelty.math;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VectorMath {
    public static Vec3 getPerpendicular(Vec3 direction) {
        var axis = direction.cross(new Vec3(1.0, 0.0, 0.0));
        if (!axis.equals(Vec3.ZERO)) return axis;

        return direction.cross(new Vec3(0.0, 1.0, 0.0));
    }

    public static Vec3 randomRotate(Vec3 direction, float angle) {
        var perpendicular = getPerpendicular(direction);
        var perpendicular3f = perpendicular.toVector3f();
        var perpendicularRotation = quaternionFromAxisAngle(perpendicular3f, angle);

        var direction3f = direction.toVector3f();
        var randomAngle = Random.nextFloat(Mth.TWO_PI);
        var randomRotation = quaternionFromAxisAngle(direction3f, randomAngle);

        direction3f.rotate(perpendicularRotation).rotate(randomRotation);
        return new Vec3(direction3f);
    }

    public static Quaternionf quaternionFromAxisAngle(Vector3f axis, float angle) {
        return new Quaternionf().fromAxisAngleRad(axis, angle);
    }

    public static Vec3 clamp(Vec3 point, AABB aabb) {
        var x = Math.clamp(point.x, aabb.minX, aabb.maxX);
        var y = Math.clamp(point.y, aabb.minY, aabb.maxY);
        var z = Math.clamp(point.z, aabb.minZ, aabb.maxZ);
        return new Vec3(x, y, z);
    }

    public static Vec3 directionTo(Vec3 from, Vec3 to) {
        return to.subtract(from).normalize();
    }

    public static Vec3 reflect(Vec3 direction, Vec3 normal) {
        return direction.subtract(normal.scale(direction.dot(normal) * 2.0));
    }
}
