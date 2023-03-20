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
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class JukeBoxEntityRenderer implements BlockEntityRenderer<JukeboxBlockEntity> {

    public JukeBoxEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(JukeboxBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.getRecord() != null && !blockEntity.getRecord().isEmpty() && AdditionMain.CONFIG.show_jukebox_disc) {
            matrices.push();
            matrices.translate(0.5D, 0.85D, 0.5D);
            matrices.scale(1.2f, 1.2f, 1.2f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getRecord(), ModelTransformation.Mode.GROUND,
                    WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up()), overlay, matrices, vertexConsumers, (int) blockEntity.getPos().asLong());
            matrices.pop();
        }
    }
}
