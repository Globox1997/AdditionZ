package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements PassiveAgeAccess {

    private int passiveAge = 0;

    public PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("PassiveAge", this.passiveAge);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.passiveAge = nbt.getInt("PassiveAge");
    }

    @Inject(method = "setBaby", at = @At("HEAD"))
    private void setBabyMixin(boolean baby, CallbackInfo info) {
        this.passiveAge = AdditionMain.CONFIG.passiveEntityConfig.baby_to_adult_time;
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovementMixin(CallbackInfo info) {
        if (!this.world.isClient)
            this.passiveAge++;
    }

    @ModifyConstant(method = "setBaby", constant = @Constant(intValue = -24000))
    private int setBabyMixin(int original) {
        return AdditionMain.CONFIG.passiveEntityConfig.baby_to_adult_time != 0 ? AdditionMain.CONFIG.passiveEntityConfig.baby_to_adult_time : original;
    }

    @Override
    public int getPassiveAge() {
        return this.passiveAge;
    }
}
