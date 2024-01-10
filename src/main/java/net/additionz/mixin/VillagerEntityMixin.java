package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.additionz.access.VillagerAccess;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements VillagerAccess {

    @Nullable
    @Unique
    private BlockPos tradingPos = null;
    @Unique
    private int tradeCount = 0;
    @Unique
    private int ironGolemCount = 0;
    @Unique
    private static final TrackedData<Boolean> MALE = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (tradingPos != null) {
            nbt.putInt("TradePosX", tradingPos.getX());
            nbt.putInt("TradePosY", tradingPos.getY());
            nbt.putInt("TradePosZ", tradingPos.getZ());
        }
        nbt.putInt("TradeCount", tradeCount);
        nbt.putInt("IronGolemCount", ironGolemCount);
        nbt.putBoolean("Male", this.dataTracker.get(MALE));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("TradePosX")) {
            tradingPos = new BlockPos(nbt.getInt("TradePosX"), nbt.getInt("TradePosY"), nbt.getInt("TradePosZ"));
        }
        tradeCount = nbt.getInt("TradeCount");
        ironGolemCount = nbt.getInt("IronGolemCount");
        this.dataTracker.set(MALE, nbt.getBoolean("Male"));
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;beginTradeWith(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (AdditionMain.CONFIG.villager_needs_space) {
            if (tradingPos == null) {
                tradingPos = new BlockPos(this.getBlockPos());
                tradeCount++;
            } else if (tradingPos.equals(this.getBlockPos())) {
                tradeCount++;
                if (tradeCount >= 5) {
                    this.sayNo();
                    info.setReturnValue(ActionResult.success(this.getWorld().isClient()));
                }
            } else {
                tradeCount = 0;
            }
        }
    }

    @Inject(method = "summonGolem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/passive/VillagerEntity;getBoundingBox()Lnet/minecraft/util/math/Box;"), cancellable = true)
    private void summonGolemMixin(ServerWorld world, long time, int requiredCount, CallbackInfo info) {
        if (AdditionMain.CONFIG.max_iron_golem_villager_spawn != 0 && ironGolemCount >= AdditionMain.CONFIG.max_iron_golem_villager_spawn) {
            info.cancel();
        } else {
            ironGolemCount++;
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTrackerMixin(CallbackInfo info) {
        this.dataTracker.startTracking(MALE, true);
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt,
            CallbackInfoReturnable<EntityData> info) {
        this.dataTracker.set(MALE, world.getRandom().nextFloat() <= 0.5F);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (MALE.equals(data)) {
            this.calculateDimensions();
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return isMaleVillager() ? super.getDimensions(pose) : super.getDimensions(pose).scaled(0.9f);
    }

    @Shadow
    private void sayNo() {
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        float multiplier = isMaleVillager() ? 1.0f : 0.9f;
        if (this.isBaby()) {
            return 0.81f * multiplier;
        }
        return 1.62f * multiplier;
    }

    @Override
    public float getSoundPitch() {
        float multiplier = isMaleVillager() ? 1.0f : 1.3f;
        if (this.isBaby()) {
            return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f) * multiplier;
        }
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * multiplier;
    }

    @Override
    public boolean isMaleVillager() {
        return this.dataTracker.get(MALE);
    }
}
