package net.additionz.mixin;

import net.additionz.AdditionMain;
import net.additionz.util.ExperienceUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

    public AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getXpToDrop", at = @At("RETURN"), cancellable = true)
    private void getXpToDropMixin(CallbackInfoReturnable<Integer> info) {
        if (AdditionMain.ENTITY_EXPERIENCE_MAP.containsKey(this.getType())) {
            info.setReturnValue(AdditionMain.ENTITY_EXPERIENCE_MAP.get(this.getType()));
        }
    }

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"), cancellable = true)
    private void breedMixin(ServerWorld world, AnimalEntity other, PassiveEntity baby, CallbackInfo info) {
        ExperienceUtil.breedingHelper(baby, info);
    }

}
