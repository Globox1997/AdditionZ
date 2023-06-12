package net.additionz.block;

import net.fabricmc.api.Environment;
import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class JukeBoxEntityRenderer implements BlockEntityRenderer<JukeboxBlockEntity> {

    public JukeBoxEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(JukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.getStack(0) != null && !blockEntity.getStack(0).isEmpty() && AdditionMain.CONFIG.show_jukebox_disc) {
            matrices.push();
            matrices.translate(0.5D, 0.85D, 0.5D);
            matrices.scale(1.2f, 1.2f, 1.2f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getStack(0), ModelTransformationMode.GROUND,
                    WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up()), overlay, matrices, vertexConsumers, blockEntity.getWorld(),
                    (int) blockEntity.getPos().asLong());
            matrices.pop();
        }
    }
}
