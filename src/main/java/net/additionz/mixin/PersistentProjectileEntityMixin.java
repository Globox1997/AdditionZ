package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    @Shadow
    protected boolean inGround;

    // @Unique
    // private List<BlockPos> piercedBlockPosList = new ArrayList<BlockPos>();
    @Unique
    private int blockPierceLevel;
    @Unique
    private boolean piercedBlock;
    @Unique
    private boolean isPearcing = true;

    @Inject(method = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    protected void PersistentProjectileEntityInitMixin(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo info) {
        blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, owner);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("BlockPierceLevel", this.blockPierceLevel);
        // nbt.putInt("PiercedBlockCount", this.piercedBlockPosList.size());
        nbt.putBoolean("PiercedBlock", this.piercedBlock);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.blockPierceLevel = nbt.getInt("BlockPierceLevel");
        // int piercedBlockCount = nbt.getInt("PiercedBlockCount");
        // this.piercedBlockPosList = new ArrayList<BlockPos>();
        // for (int i = 0; i < piercedBlockCount; i++)
        // this.piercedBlockPosList.add(new BlockPos(0, i, 0));
        this.piercedBlock = nbt.getBoolean("PiercedBlock");

    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    protected void onBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info) {
        // if (blockPierceLevel > 0 && (blockPierceLevel > piercedBlockPosList.size()
        // || (!piercedBlockPosList.isEmpty() && !piercedBlockPosList.get(piercedBlockPosList.size() - 1).equals(blockHitResult.getBlockPos())))) {
        // // System.out.println(piercedBlockPosList + " : " + piercedBlockPosList.size());
        // if (!piercedBlockPosList.contains(blockHitResult.getBlockPos()))
        // piercedBlockPosList.add(blockHitResult.getBlockPos());

        // info.cancel();
        // }
        if (blockPierceLevel > 0 && this.isPearcing) {
            this.piercedBlock = true;
            info.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"))
    private void tickMixin(CallbackInfo info) {
        // if (this.inGround && blockPierceLevel > 0 && blockPierceLevel > piercedBlockPosList.size())
        // this.inGround = false;

        if (this.piercedBlock && !((PersistentProjectileEntity) (Object) this).isInsideWall())
            this.isPearcing = false;

        if (this.inGround && blockPierceLevel > 0 && this.isPearcing)
            this.inGround = false;
    }

    // Not called when shooting arrow
    @Inject(method = "applyEnchantmentEffects", at = @At("HEAD"), cancellable = true)
    private void applyEnchantmentEffectsMixin(LivingEntity entity, float damageModifier, CallbackInfo info) {
        blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, entity);

    }

    // net/minecraft/entity/projectile/PersistentProjectileEntity.isTouchingWaterOrRain
    // ()Z
    // @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"),
    // print = true)
    // private boolean tickModifyMixin(boolean original) {
    // return original;
    // }

    // protected void onBlockHit(BlockHitResult blockHitResult) {
    // BlockState blockState =
    // this.world.getBlockState(blockHitResult.getBlockPos());
    // blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
    // }
}
