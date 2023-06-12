package net.additionz.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Degradable;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Mixin(Degradable.class)
public interface DegradableMixin {

    @Inject(method = "tryDegrade", at = @At("HEAD"), cancellable = true)
    default public void tryDegradeMixin(BlockState state2, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        if (AdditionMain.CONFIG.fast_oxidization) {
            boolean hasFire = false;
            boolean hasWater = false;
            for (int i = 0; i < AdditionMain.DIRECTIONS.size(); i++) {
                if (world.getBlockState(pos.offset(AdditionMain.DIRECTIONS.get(i))).isIn(BlockTags.FIRE) || world.getBlockState(pos.offset(AdditionMain.DIRECTIONS.get(i))).isOf(Blocks.LAVA)) {
                    hasFire = true;
                }
                if (world.getBlockState(pos.offset(AdditionMain.DIRECTIONS.get(i))).isOf(Blocks.WATER)) {
                    hasWater = true;
                }
                if (hasFire && hasWater) {
                    break;
                }
            }

            if (hasFire && hasWater && random.nextFloat() < 0.7F) {
                this.getDegradationResult(state2).ifPresent(state -> world.setBlockState(pos, (BlockState) state));
                info.cancel();
            }
        }
    }

    @Shadow
    public Optional<BlockState> getDegradationResult(BlockState var1);

}
