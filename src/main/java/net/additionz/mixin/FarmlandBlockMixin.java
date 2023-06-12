package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShovelItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void onLandedUponMixin(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo info) {
        if (AdditionMain.CONFIG.feather_falling_trample && EnchantmentHelper.getEquipmentLevel(Enchantments.FEATHER_FALLING, (LivingEntity) entity) > 0) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
            info.cancel();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (AdditionMain.CONFIG.shovel_undo_farmland && player.getStackInHand(hand).getItem() instanceof ShovelItem) {
            if (world.getBlockState(pos.up()).isAir() && !FarmlandBlockMixin.hasCrop(world, pos)) {
                world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (!world.isClient()) {
                    FarmlandBlock.setToDirt(player, state, world, pos);
                    player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
                }
                return ActionResult.success(world.isClient);
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Shadow
    private static boolean hasCrop(BlockView world, BlockPos pos) {
        return false;
    }
}
