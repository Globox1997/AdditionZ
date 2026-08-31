package net.additionz.mixin.client;

import net.additionz.access.PassiveAgeAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    public LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "scale", at = @At("HEAD"))
    private void scaleMixin(T entity, MatrixStack matrices, float amount, CallbackInfo info) {
        if (entity instanceof PassiveAgeAccess passiveAgeAccess && passiveAgeAccess.isImmature()) {
            matrices.scale(0.8F, 0.8F, 0.8F);
        }
    }

}
