package com.xm666.realisticcruelty.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

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

        builder.addAll(getBlockCollisions(level, entity, boundingBox));
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

    private static Iterable<VoxelShape> getBlockCollisions(CollisionGetter collisionGetter, @Nullable Entity entity, AABB collisionBox) {
        return () -> new BlockCollisions<>(collisionGetter, entity, collisionBox, false, (mutableBlockPos, voxelShape) -> voxelShape);
    }

    private static class BlockCollisions<T> extends AbstractIterator<T> {
        private final AABB box;
        private final CollisionContext context;
        private final Cursor3D cursor;
        private final BlockPos.MutableBlockPos pos;
        private final VoxelShape entityShape;
        private final CollisionGetter collisionGetter;
        private final boolean onlySuffocatingBlocks;
        private final BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider;
        @Nullable
        private BlockGetter cachedBlockGetter;
        private long cachedBlockGetterPos;

        public BlockCollisions(CollisionGetter collisionGetter, @Nullable Entity entity, AABB box, boolean onlySuffocatingBlocks, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider) {
            this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
            this.pos = new BlockPos.MutableBlockPos();
            this.entityShape = Shapes.create(box);
            this.collisionGetter = collisionGetter;
            this.box = box;
            this.onlySuffocatingBlocks = onlySuffocatingBlocks;
            this.resultProvider = resultProvider;
            var minX = Mth.floor(box.minX - 1.0E-7) - 1;
            var maxX = Mth.floor(box.maxX + 1.0E-7) + 1;
            var minY = Mth.floor(box.minY - 1.0E-7) - 1;
            var maxY = Mth.floor(box.maxY + 1.0E-7) + 1;
            var minZ = Mth.floor(box.minZ - 1.0E-7) - 1;
            var maxZ = Mth.floor(box.maxZ + 1.0E-7) + 1;
            this.cursor = new Cursor3D(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Nullable
        private BlockGetter getChunk(int x, int z) {
            var sectionX = SectionPos.blockToSectionCoord(x);
            var sectionZ = SectionPos.blockToSectionCoord(z);
            var chunkPos = ChunkPos.asLong(sectionX, sectionZ);
            if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == chunkPos) {
                return this.cachedBlockGetter;
            } else {
                var blockGetter = this.collisionGetter.getChunkForCollisions(sectionX, sectionZ);
                this.cachedBlockGetter = blockGetter;
                this.cachedBlockGetterPos = chunkPos;
                return blockGetter;
            }
        }

        @Override
        protected T computeNext() {
            while (this.cursor.advance()) {
                var x = this.cursor.nextX();
                var y = this.cursor.nextY();
                var z = this.cursor.nextZ();
                var type = this.cursor.getNextType();
                if (type != Cursor3D.TYPE_CORNER) {
                    var blockGetter = this.getChunk(x, z);
                    if (blockGetter != null) {
                        this.pos.set(x, y, z);
                        var blockState = blockGetter.getBlockState(this.pos);
                        if ((!this.onlySuffocatingBlocks || blockState.isSuffocating(blockGetter, this.pos))
                                && (type != Cursor3D.TYPE_FACE || blockState.hasLargeCollisionShape())
                                && (type != Cursor3D.TYPE_EDGE || blockState.is(Blocks.MOVING_PISTON))) {
                            var voxelShape = blockState.getShape(this.collisionGetter, this.pos, this.context);
                            if (voxelShape == Shapes.block()) {
                                if (this.box.intersects(x, y, z, (double) x + 1.0, (double) y + 1.0, (double) z + 1.0)) {
                                    return this.resultProvider.apply(this.pos, voxelShape.move(x, y, z));
                                }
                            } else {
                                var transformedVoxelShape = voxelShape.move(x, y, z);
                                if (!transformedVoxelShape.isEmpty() && Shapes.joinIsNotEmpty(transformedVoxelShape, this.entityShape, BooleanOp.AND)) {
                                    return this.resultProvider.apply(this.pos, transformedVoxelShape);
                                }
                            }
                        }
                    }
                }
            }

            return this.endOfData();
        }
    }
}
