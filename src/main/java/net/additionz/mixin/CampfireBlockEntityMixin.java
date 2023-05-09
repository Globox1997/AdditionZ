package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(value = CampfireBlockEntity.class, priority = 1001)
public abstract class CampfireBlockEntityMixin {

    private int rainBurnTime = 0;

    @Inject(method = "litServerTick", at = @At("TAIL"))
    private static void litServerTickMixin(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo info) {
        if (world.getTime() % 20 == 0 && AdditionMain.CONFIG.campfire_rain_extinguish != 0 && world.isRaining() && world.isSkyVisible(pos)) {
            ((CampfireBlockEntityMixin) (Object) campfire).rainBurnTime++;
            if (((CampfireBlockEntityMixin) (Object) campfire).rainBurnTime > AdditionMain.CONFIG.campfire_rain_extinguish
                    && world.getRandom().nextInt(AdditionMain.CONFIG.campfire_rain_extinguish) == 0) {
                CampfireBlock.extinguish(null, world, pos, state);
                world.setBlockState(pos, state.with(CampfireBlock.LIT, false), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            }
        }
    }

    @Inject(method = "unlitServerTick", at = @At("TAIL"))
    private static void unlitServerTickMixin(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo info) {
        if (AdditionMain.CONFIG.campfire_rain_extinguish != 0 && ((CampfireBlockEntityMixin) (Object) campfire).rainBurnTime > 0) {
            ((CampfireBlockEntityMixin) (Object) campfire).rainBurnTime = 0;
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo info) {
        this.rainBurnTime = nbt.getInt("RainBurnTime");
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    protected void writeNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("RainBurnTime", this.rainBurnTime);
    }
}
