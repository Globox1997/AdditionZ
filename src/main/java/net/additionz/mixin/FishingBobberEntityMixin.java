package net.additionz.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.additionz.util.ExperienceUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FishingBobberEntity.class, priority = 1098)
public class FishingBobberEntityMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 1))
    private boolean useMixin(World instance, Entity entity, Operation<Boolean> original, @Local ItemEntity itemEntity) {
        return ExperienceUtil.fishingHelper(instance, entity, itemEntity, original);
    }

}
