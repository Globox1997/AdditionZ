package net.additionz.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.additionz.access.VillagerAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;

@Mixin(VillagerBreedTask.class)
public class VillagerBreedTaskMixin {

    @Inject(method = "isReadyToBreed", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void isReadyToBreed(VillagerEntity villager, CallbackInfoReturnable<Boolean> info, Brain<VillagerEntity> brain, Optional<PassiveEntity> optional) {
        if (AdditionMain.CONFIG.villager_gender && optional.isPresent() && ((VillagerAccess) villager).isMaleVillager() == ((VillagerAccess) optional.get()).isMaleVillager()) {
            Optional<PassiveEntity> otherOptional = brain.getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).filter(passiveEntity -> {
                return passiveEntity.getType() == EntityType.VILLAGER && ((VillagerAccess) villager).isMaleVillager() != ((VillagerAccess) passiveEntity).isMaleVillager();
            });
            if (!otherOptional.isPresent()) {
                info.setReturnValue(false);
            }
            optional = otherOptional;
        }
    }

}
