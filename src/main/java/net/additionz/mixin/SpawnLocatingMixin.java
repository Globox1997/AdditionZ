package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

@Mixin(SpawnLocating.class)
public class SpawnLocatingMixin {

    @Inject(method = "findOverworldSpawn", at = @At("RETURN"), cancellable = true)
    private static void findOverworldSpawnMixin(ServerWorld world, int x, int z, CallbackInfoReturnable<BlockPos> info) {
        if (info.getReturnValue() != null && AdditionMain.CONFIG.spawn_not_on_leaves) {
            if (world.getBlockState(info.getReturnValue().down()).isIn(BlockTags.LEAVES)) {
                for (int o = 2; o < 30; o++) {
                    if (world.getBlockState(info.getReturnValue().down(o)).isAir() && world.getBlockState(info.getReturnValue().down(o + 1)).isAir()
                            && !world.getBlockState(info.getReturnValue().down(o + 2)).isAir() && !world.getBlockState(info.getReturnValue().down(o + 2)).isIn(BlockTags.LEAVES)
                            && world.getBlockState(info.getReturnValue().down(o + 2)).getFluidState().isEmpty()) {
                        info.setReturnValue(info.getReturnValue());
                    }
                }
                info.setReturnValue(null);
            }
        }
    }

}
