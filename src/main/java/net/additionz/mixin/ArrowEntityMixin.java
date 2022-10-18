package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends PersistentProjectileEntity {

    public ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        if (!this.world.isClient && AdditionMain.CONFIG.flame_arrow_burn) {
            BlockPos blockPos2 = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos2, this.getHorizontalFacing())) {
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            }
        }
    }

}
