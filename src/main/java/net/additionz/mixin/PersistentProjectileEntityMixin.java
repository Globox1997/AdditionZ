package net.additionz.mixin;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {

    @Shadow
    protected boolean inGround;
    @Shadow
    private int life;

    @Unique
    private int blockPierceLevel;
    @Unique
    private boolean isPearcing = true;
    @Unique
    private List<BlockPos> piercedBlockPosList = new ArrayList<BlockPos>();

    @Inject(method = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
    protected void PersistentProjectileEntityInitMixin(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo info) {
        this.blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, owner);
        if (this.blockPierceLevel == 0)
            this.isPearcing = false;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("BlockPierceLevel", this.blockPierceLevel);
        nbt.putInt("PiercedBlockCount", this.piercedBlockPosList.size());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.blockPierceLevel = nbt.getInt("BlockPierceLevel");
        int piercedBlockCount = nbt.getInt("PiercedBlockCount");
        this.piercedBlockPosList = new ArrayList<BlockPos>();
        for (int i = 0; i < piercedBlockCount; i++)
            this.piercedBlockPosList.add(new BlockPos(0, i, 0));
        if (blockPierceLevel <= piercedBlockCount || blockPierceLevel == 0)
            this.isPearcing = false;

    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    protected void onBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info) {
        if (!((PersistentProjectileEntity) (Object) this).world.isClient && this.isPearcing)
            info.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"))
    private void tickMixin(CallbackInfo info) {
        if (!((PersistentProjectileEntity) (Object) this).world.isClient && this.isPearcing) {
            Vec3d vec3d = ((PersistentProjectileEntity) (Object) this).getVelocity();
            Vec3d pos = ((PersistentProjectileEntity) (Object) this).getPos();
            if ((int) Math.round(vec3d.length()) <= this.blockPierceLevel + 1)
                for (int i = 0; i < 100; i++) {
                    vec3d = vec3d.multiply(1.05D);
                    if ((int) Math.round(vec3d.length()) > this.blockPierceLevel + 1)
                        break;
                }
            Vec3d futurePos = ((PersistentProjectileEntity) (Object) this).getPos().add(vec3d);

            int distance = (int) Math.round(vec3d.length());

            checkFutureBlocks(pos, futurePos, distance);

            if (this.piercedBlockPosList.size() < this.blockPierceLevel + 1 && !((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(futurePos)).isAir()) {
                Vec3d futureFuturePos = futurePos.add(vec3d);
                checkFutureBlocks(futurePos, futureFuturePos, distance);
            }

            if (this.piercedBlockPosList.size() >= this.blockPierceLevel + 1) {
                // System.out.println("CHECK: " + ((PersistentProjectileEntity) (Object) this).getPos() + " : " + ((PersistentProjectileEntity) (Object) this).world.getTime());
                this.isPearcing = false;
            } else if (this.inGround)
                this.inGround = false;
        }

    }

    // Not called when shooting arrow
    @Inject(method = "applyEnchantmentEffects", at = @At("HEAD"), cancellable = true)
    private void applyEnchantmentEffectsMixin(LivingEntity entity, float damageModifier, CallbackInfo info) {
        this.blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, entity);
        if (this.blockPierceLevel == 0)
            this.isPearcing = false;
    }

    // private void checkFutureBlocks(Vec3d pos, Vec3d futurePos, int distance) {
    // BlockPos oldPos = null;
    // for (int i = 1; i <= distance; i++) {

    // BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
    // if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(blockPos).isAir() && !this.piercedBlockPosList.contains(blockPos))
    // this.piercedBlockPosList.add(blockPos);

    // if (oldPos != null && this.piercedBlockPosList.size() >= this.blockPierceLevel + 1 && ((PersistentProjectileEntity) (Object) this).world.getBlockState(oldPos).isAir()) {

    // ((PersistentProjectileEntity) (Object) this).setPos(oldPos.getX(), oldPos.getY(), oldPos.getZ());

    // System.out.println(((PersistentProjectileEntity) (Object) this).getPos());
    // break;
    // }
    // oldPos = blockPos;
    // }
    // }

    private void checkFutureBlocks(Vec3d pos, Vec3d futurePos, int distance) {
        // BlockPos oldPos = null;
        ArrayList<Vec3d> oldPosList = new ArrayList<>();
        for (int i = 1; i <= distance; i++) {
            // System.out.println("OKAY: " + (1.0D / distance * i) + " : " + (distance * i));

            // BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
            Vec3d differentPos = pos.lerp(futurePos, 1.0D / distance * i);
            BlockPos blockPos = new BlockPos(differentPos);

            // if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(blockPos).isAir() && !this.piercedBlockPosList.contains(blockPos))
            // this.piercedBlockPosList.add(blockPos);
            if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(blockPos).isAir() && !this.piercedBlockPosList.contains(blockPos))
                this.piercedBlockPosList.add(blockPos);

            if (!oldPosList.contains(differentPos))
                oldPosList.add(differentPos);
            // oldPos = blockPos;
            // oldPos = differentPos;
        }
        if (!oldPosList.isEmpty() && this.piercedBlockPosList.size() >= this.blockPierceLevel + 1
                && !((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(futurePos)).isAir()) {
            for (int i = 0; i < oldPosList.size(); i++) {
                BlockPos newPos = new BlockPos(oldPosList.get(i));
                if (((PersistentProjectileEntity) (Object) this).world.getBlockState(newPos).isAir()) {
                    // System.out.println("B: " + ((PersistentProjectileEntity) (Object) this).getPos());
                    ((PersistentProjectileEntity) (Object) this).setPos(oldPosList.get(i).getX(), oldPosList.get(i).getY(), oldPosList.get(i).getZ());
                    // System.out.println("A: " + ((PersistentProjectileEntity) (Object) this).getPos());
                    break;
                }
            }

            // && ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(oldPos)).isAir()

            // System.out.println(
            // "OKAY: " + blockPos + " : " + oldPos + " : " + ((PersistentProjectileEntity) (Object) this).world.getTime() + " : " + ((PersistentProjectileEntity) (Object) this).getPos());

            // System.out.println(
            // "AFTER: " + blockPos + " : " + oldPos + " : " + ((PersistentProjectileEntity) (Object) this).world.getTime() + " : " + ((PersistentProjectileEntity) (Object) this).getPos());
            // // ((PersistentProjectileEntity) (Object) this).teleport(oldPos.getX(), oldPos.getY(), oldPos.getZ());
            // break;
        }
    }
}
