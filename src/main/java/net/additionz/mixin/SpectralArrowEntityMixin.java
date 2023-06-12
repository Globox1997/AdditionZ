package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin extends PersistentProjectileEntity {

    @Nullable
    private BlockPos lightPos = null;

    public SpectralArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("LightPosX"))
            this.lightPos = new BlockPos(nbt.getInt("LightPosX"), nbt.getInt("LightPosY"), nbt.getInt("LightPosZ"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (this.lightPos != null) {
            nbt.putInt("LightPosX", this.lightPos.getX());
            nbt.putInt("LightPosY", this.lightPos.getY());
            nbt.putInt("LightPosZ", this.lightPos.getZ());
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickMixin(CallbackInfo info) {
        if (!this.getWorld().isClient() && this.lightPos != null && !this.inGround && this.getWorld().getBlockState(this.lightPos).isOf(Blocks.LIGHT)) {
            this.getWorld().setBlockState(this.lightPos, Blocks.AIR.getDefaultState());
            this.lightPos = null;
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient() && AdditionMain.CONFIG.spectral_arrow_light && this.getWorld().getBlockState(this.getBlockPos()).isAir()) {
            this.lightPos = this.getBlockPos();
            this.getWorld().setBlockState(this.getBlockPos(), Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 10));
        }

    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient() && lightPos != null && this.getWorld().getBlockState(this.lightPos).isOf(Blocks.LIGHT))
            this.getWorld().setBlockState(this.lightPos, Blocks.AIR.getDefaultState());

        super.remove(reason);
    }

}
