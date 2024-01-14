package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.world.World;

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

}
