package net.additionz.mixin;

import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements PassiveAgeAccess {

    @Shadow
    public abstract boolean isBaby();

    @Shadow
    protected abstract void initDataTracker(DataTracker.Builder builder);

    @Unique
    private static final TrackedData<Boolean> IMMATURE = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private int passiveAge = 0;

    @Unique
    private boolean gotDamaged = false;
    @Unique
    private long damageTime;

    public PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerMixin(DataTracker.Builder builder, CallbackInfo info) {
        builder.add(IMMATURE, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("PassiveAge", this.passiveAge);
        nbt.putBoolean("GotDamaged", this.gotDamaged);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.passiveAge = nbt.getInt("PassiveAge");
        this.dataTracker.set(IMMATURE, this.passiveAge < 0 && this.passiveAge > -AdditionMain.CONFIG.baby_grow_time);
        this.gotDamaged = nbt.getBoolean("GotDamaged");
    }

    @Inject(method = "setBaby", at = @At("HEAD"))
    private void setBabyMixin(boolean baby, CallbackInfo info) {
        this.passiveAge = -AdditionMain.CONFIG.baby_grow_time * 2;
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovementMixin(CallbackInfo info) {
        if (!this.getWorld().isClient()) {

            if (this.passiveAge < 0 && AdditionMain.CONFIG.baby_grow_time != 0) {
                this.passiveAge += 1;

                if (this.passiveAge > -AdditionMain.CONFIG.baby_grow_time && !this.getDataTracker().get(IMMATURE)) {
                    this.getDataTracker().set(IMMATURE, true);
                }
                if (this.passiveAge == 0) {
                    this.getDataTracker().set(IMMATURE, false);
                }
            }
            if (AdditionMain.CONFIG.heal_passive_entity_over_time_ticks > 0) {
                if ((this.getWorld().getTime() - this.damageTime) % AdditionMain.CONFIG.heal_passive_entity_over_time_ticks == 0)
                    if (this.getMaxHealth() > this.getHealth() && this.gotDamaged) {
                        this.heal(2f);
                    } else if (this.gotDamaged) {
                        this.gotDamaged = false;
                    }
                if (!this.gotDamaged && this.getWorld().getTime() % 20 == 0 && this.getAttacker() != null) {
                    this.damageTime = this.getWorld().getTime();
                    this.gotDamaged = true;
                }
            }
        }
    }

    @ModifyConstant(method = "setBaby", constant = @Constant(intValue = -24000))
    private int setBabyMixin(int original) {
        return AdditionMain.CONFIG.baby_grow_time != 0 ? (-AdditionMain.CONFIG.baby_grow_time) : original;
    }

    @Override
    public boolean isImmature() {
        if (this.getWorld().isClient()) {
            return this.dataTracker.get(IMMATURE);
        } else {
            return this.passiveAge < 0 && this.passiveAge > -AdditionMain.CONFIG.baby_grow_time;
        }
    }
}
