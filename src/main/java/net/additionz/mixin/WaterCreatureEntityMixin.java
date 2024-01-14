package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.world.World;

@Mixin(WaterCreatureEntity.class)
public abstract class WaterCreatureEntityMixin extends PathAwareEntity {

    public WaterCreatureEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getXpToDrop", at = @At("RETURN"), cancellable = true)
    private void getXpToDropMixin(CallbackInfoReturnable<Integer> info) {
        if (AdditionMain.ENTITY_EXPERIENCE_MAP.containsKey(this.getType())) {
            info.setReturnValue(AdditionMain.ENTITY_EXPERIENCE_MAP.get(this.getType()));
        }
    }

}
