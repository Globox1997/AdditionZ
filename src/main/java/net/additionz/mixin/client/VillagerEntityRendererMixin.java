package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.additionz.misc.VillagerEntitySleepingFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;

@Environment(EnvType.CLIENT)
@Mixin(VillagerEntityRenderer.class)
public abstract class VillagerEntityRendererMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    public VillagerEntityRendererMixin(Context context, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/VillagerEntityRenderer;addFeature(Lnet/minecraft/client/render/entity/feature/FeatureRenderer;)Z"))
    private void initMixin(EntityRendererFactory.Context ctx, CallbackInfo info) {
        if (AdditionMain.CONFIG.villager_sleeping_eyes)
            this.addFeature(new VillagerEntitySleepingFeatureRenderer((VillagerEntityRenderer) (Object) this));
    }

}
