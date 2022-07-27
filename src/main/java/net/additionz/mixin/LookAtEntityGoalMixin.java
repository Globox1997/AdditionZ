package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(LookAtEntityGoal.class)
public class LookAtEntityGoalMixin {

    @Shadow
    @Nullable
    protected Entity target;

    @Inject(method = "canStart", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void canStartMixin(CallbackInfoReturnable<Boolean> info) {
        if (this.target != null && AdditionMain.CONFIG.not_look_at_invisible && this.target instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY))
            info.setReturnValue(false);
    }
}
