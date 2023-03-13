package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {

    @Shadow
    private int spawnCount;

    private int totalSpawnCount = 0;
    private int deactivationTicks = 0;

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbtMixin(@Nullable World world, BlockPos pos, NbtCompound nbt, CallbackInfo info) {
        this.totalSpawnCount = nbt.getInt("TotalSpawnCount");
        this.deactivationTicks = nbt.getInt("DeactivationTicks");
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void writeNbtMixin(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
        nbt.putInt("TotalSpawnCount", this.totalSpawnCount);
        nbt.putInt("DeactivationTicks", this.deactivationTicks);
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/nbt/NbtCompound;getList(Ljava/lang/String;I)Lnet/minecraft/nbt/NbtList;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void serverTickMixin(ServerWorld world, BlockPos pos, CallbackInfo info, boolean bl, int i) {
        if (this.deactivationTicks > 0) {
            info.cancel();
        } else if (AdditionMain.CONFIG.max_spawner_count != 0 && i == spawnCount - 1) {
            this.totalSpawnCount++;
        }
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private void serverTickDeactivationMixin(ServerWorld world, BlockPos pos, CallbackInfo info) {
        if (AdditionMain.CONFIG.spawner_tick_deactivation != 0 && this.deactivationTicks > 0) {
            this.deactivationTicks--;
        }
    }

    @Inject(method = "serverTick", at = @At("TAIL"))
    private void serverTickMixin(ServerWorld world, BlockPos pos, CallbackInfo info) {
        if (AdditionMain.CONFIG.max_spawner_count != 0 && this.totalSpawnCount >= AdditionMain.CONFIG.max_spawner_count) {
            if (AdditionMain.CONFIG.spawner_tick_deactivation != 0) {
                this.deactivationTicks = AdditionMain.CONFIG.spawner_tick_deactivation;
            } else {
                world.breakBlock(pos, true);
            }
        }
    }

}
