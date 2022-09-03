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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Nullable
    @Unique
    private BlockPos tradingPos = null;
    @Unique
    private int tradeCount = 0;

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
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("TradePosX"))
            tradingPos = new BlockPos(nbt.getInt("TradePosX"), nbt.getInt("TradePosY"), nbt.getInt("TradePosZ"));
        tradeCount = nbt.getInt("TradeCount");
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;beginTradeWith(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (AdditionMain.CONFIG.villager_needs_space)
            if (tradingPos == null) {
                tradingPos = new BlockPos(this.getBlockPos());
                tradeCount++;
            } else if (tradingPos.equals(this.getBlockPos())) {
                tradeCount++;
                if (tradeCount >= 5) {
                    this.sayNo();
                    info.setReturnValue(ActionResult.success(this.world.isClient));
                }
            } else
                tradeCount = 0;
    }

    @Shadow
    private void sayNo() {
    }
}
