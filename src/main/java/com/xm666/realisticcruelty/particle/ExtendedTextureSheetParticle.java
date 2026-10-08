package com.xm666.realisticcruelty.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class ExtendedTextureSheetParticle extends TextureSheetParticle {
    protected ExtendedTextureSheetParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_XYZ;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        var vec3 = camera.getPosition();
        var f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        var f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        var f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        var quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, camera, partialTicks);
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        this.renderRotatedQuad(buffer, quaternionf, f, f1, f2, partialTicks);
    }

    protected void renderRotatedQuad(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        var avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        var f3 = this.getQuadSize(partialTicks);

        for (var i = 0; i < 4; ++i) {
            var vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f3);
            vector3f.add(x, y, z);
        }

        var f6 = this.getU0();
        var f7 = this.getU1();
        var f4 = this.getV0();
        var f5 = this.getV1();
        var j = this.getLightColor(partialTicks);
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public interface FacingCameraMode {
        FacingCameraMode LOOKAT_XYZ = (quaternionf, camera, partialTick) -> quaternionf.set(camera.rotation());
        FacingCameraMode LOOKAT_Y = (quaternionf, camera, partialTick) -> quaternionf.set(0.0F, camera.rotation().y, 0.0F, camera.rotation().w);

        void setRotation(Quaternionf quaternionf, Camera camera, float partialTick);
    }
}
