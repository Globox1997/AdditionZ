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
            for (int i = 1; i <= distance; i++) {
                BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
                if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(blockPos).isAir() && !this.piercedBlockPosList.contains(blockPos))
                    this.piercedBlockPosList.add(blockPos);
            }
            if (this.piercedBlockPosList.size() >= this.blockPierceLevel + 1 || !((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(futurePos)).isAir()) {
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
}
