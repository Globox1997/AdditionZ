package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.additionz.access.VillagerAccess;
import net.additionz.misc.VillagerEntitySleepingFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(VillagerEntityRenderer.class)
public abstract class VillagerEntityRendererMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    private static final Identifier FEMALE_TEXTURE = new Identifier("textures/entity/villager/female_villager.png");

    public VillagerEntityRendererMixin(Context context, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/VillagerEntityRenderer;addFeature(Lnet/minecraft/client/render/entity/feature/FeatureRenderer;)Z"))
    private void initMixin(EntityRendererFactory.Context ctx, CallbackInfo info) {
        if (AdditionMain.CONFIG.villager_sleeping_eyes) {
            this.addFeature(new VillagerEntitySleepingFeatureRenderer((VillagerEntityRenderer) (Object) this));
        }
    }

    @Inject(method = "getTexture", at = @At("RETURN"), cancellable = true)
    private void getTextureMixin(VillagerEntity villagerEntity, CallbackInfoReturnable<Identifier> info) {
        if (AdditionMain.CONFIG.villager_gender && !((VillagerAccess) villagerEntity).isMaleVillager()) {
            info.setReturnValue(FEMALE_TEXTURE);
        }
    }

    @Inject(method = "scale", at = @At("TAIL"))
    protected void scale(VillagerEntity villagerEntity, MatrixStack matrixStack, float f, CallbackInfo info) {
        if (AdditionMain.CONFIG.villager_gender && !((VillagerAccess) villagerEntity).isMaleVillager()) {
            matrixStack.scale(0.9f, 0.9f, 0.9f);
        }
    }

    @Override
    public void render(VillagerEntity villagerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ModelPart nose = ((VillagerAccess) this.getModel()).getNose();
        if (!((VillagerAccess) villagerEntity).isMaleVillager()) {
            nose.pivotY = -2.3f;
            nose.pivotZ = -1.2f;
            nose.xScale = 0.7f;
            nose.yScale = 0.7f;
            nose.zScale = 0.7f;
        } else {
            nose.pivotY = -2.0f;
            nose.pivotZ = 0.0f;
            nose.xScale = 1.0f;
            nose.yScale = 1.0f;
            nose.zScale = 1.0f;
        }
        super.render(villagerEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

}
