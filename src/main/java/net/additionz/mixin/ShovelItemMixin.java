package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    // Could redirect world.getBlockState(blockPos.up()).isAir() but inject makes it compatible
    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info, World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity,
            BlockState blockState2, BlockState blockState3) {
        if (AdditionMain.CONFIG.path_block_under_gates && world.getBlockState(blockPos.up()).getBlock() instanceof FenceGateBlock) {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient) {
                blockState3 = blockState2;
                world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, blockState3));
                if (playerEntity != null)
                    context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
            }
            info.setReturnValue(ActionResult.success(world.isClient));
        }
    }
}
