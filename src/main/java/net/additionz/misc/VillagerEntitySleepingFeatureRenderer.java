package net.additionz.misc;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerEntitySleepingFeatureRenderer extends FeatureRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {
    private static final RenderLayer EYE_LAYER = RenderLayer.getEntityCutoutNoCull(new Identifier("additionz", "textures/entity/sleeping_villager_eyes.png"));

    public VillagerEntitySleepingFeatureRenderer(FeatureRendererContext<VillagerEntity, VillagerResemblingModel<VillagerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, VillagerEntity villagerEntity, float f, float g, float h, float j, float k, float l) {
        if (villagerEntity.isSleeping()) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(EYE_LAYER);
            RenderSystem.enableBlend();
            this.getContextModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
        }
    }

}