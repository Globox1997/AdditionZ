package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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

    @ModifyConstant(method = "tickDegradation", constant = @Constant(floatValue = 0.05688889f, ordinal = 1))
    default public float tickDegradationModifyMixin(float original, BlockState state, ServerWorld world, BlockPos pos, Random random) {
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
                tryDegrade(state, world, pos, random);
            }
        }
        return original;
    }

    @Shadow
    default public void tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

}
