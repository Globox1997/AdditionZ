package net.additionz.block.render;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.additionz.block.entity.ChunkLoaderEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChunkLoaderRenderer implements BlockEntityRenderer<ChunkLoaderEntity> {

    public ChunkLoaderRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ChunkLoaderEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if (entity != null && !entity.isUpgradeEmpty()) {
            float h = MathHelper.cos((float) entity.getWorld().getTime() * 0.01f) * 360f;
            float k = MathHelper.sin(entity.getWorld().getTime() * 0.1f) / 2.0f + 0.5f;
            k = k * k + k;
            matrices.push();
            matrices.translate(0.5f, 0.4f + k * 0.1f, 0.5f);
            matrices.scale(0.8f, 0.8f, 0.8f);
            Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f).normalize();
            matrices.multiply(new Quaternionf().rotationAxis(h * ((float) Math.PI / 180), vector3f));

            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getUpgradeStack(), ModelTransformationMode.FIXED, 14680064, overlay, matrices, vertexConsumerProvider, entity.getWorld(),
                    (int) entity.getPos().asLong());
            matrices.pop();
        }
    }
}
