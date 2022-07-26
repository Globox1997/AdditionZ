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
    // @Unique
    // private boolean piercedBlock;
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
        // nbt.putBoolean("PiercedBlock", this.piercedBlock);
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
        // this.piercedBlock = nbt.getBoolean("PiercedBlock");

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
        // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());
        // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());
        if (!((PersistentProjectileEntity) (Object) this).world.isClient && this.isPearcing)
            info.cancel();

        // if (blockPierceLevel > 0 && this.isPearcing) {
        // // this.piercedBlock = true;
        // Vec3d vec3d = ((PersistentProjectileEntity) (Object) this).getVelocity();
        // if (Math.round(vec3d.length()) >= 2.0D) {
        // // Vec3d normalizeVec3d = vec3d.normalize();

        // Vec3d pos = ((PersistentProjectileEntity) (Object) this).getPos();
        // Vec3d futurePos = ((PersistentProjectileEntity) (Object) this).getPos().add(vec3d);

        // System.out.println(
        // ((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + pos + " : " + futurePos + " Round: " + Math.round(vec3d.length()) + " : Length: " + vec3d.length());

        // int distance = (int) Math.round(vec3d.length());
        // int futurePiercingBlockCount = ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos().isAir() ? 0 : 1;
        // for (int i = 1; i <= distance; i++) {
        // // System.out.println(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)) + " : " + 1.0D / distance * i + " : " + pos.lerp(futurePos, 1.0D / distance * i));
        // // BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
        // // ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)));
        // if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i))).isAir())
        // futurePiercingBlockCount++;
        // if (futurePiercingBlockCount >= blockPierceLevel) {
        // this.isPearcing = false;
        // // return super.onBlockHit(blockHitResult);
        // }
        // }

        // // for (double i = 0; i <= Math.round(1.0D / vec3d.length()); i++) {

        // // System.out.println(new BlockPos(pos.lerp(futurePos, 1.0D / vec3d.length())));
        // // }

        // // vec3d.add(normalizeVec3d);
        // // BlockState blockState = ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos();

        // // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + blockState);

        // // if (blockState.isAir()) {
        // // vec3d.add(normalizeVec3d.normalize());
        // // System.out.println("isAir: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));
        // // vec3d.add(normalizeVec3d.normalize());
        // // System.out.println("isAir2: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));
        // // } else {
        // // System.out.println("Not Air!: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));

        // // // System.out.println(((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().add(vec3i)) + " : " + vec3i);
        // // }

        // // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos() + " - Time: "
        // // + ((PersistentProjectileEntity) (Object) this).world.getTime());
        // // // System.out.println(((PersistentProjectileEntity) (Object) this).getVelocity().);
        // // System.out.println(((PersistentProjectileEntity) (Object) this).getVelocity().normalize());
        // // System.out.println(((PersistentProjectileEntity) (Object) this).world
        // // .getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().offset(((PersistentProjectileEntity) (Object) this).getMovementDirection().getOpposite(), 1)) + " : "
        // // + ((PersistentProjectileEntity) (Object) this).getMovementDirection());
        // // Vec3i vec3i = new Vec3i(Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().x),
        // // Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().y), Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().z));
        // // System.out.println(((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().add(vec3i)) + " : " + vec3i);
        // System.out.println("=====");
        // // ((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().)
        // // this.world.getBlockState(this.getBlockPos())
        // if (this.isPearcing)
        // info.cancel();
        // }
        // }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"))
    private void tickMixin(CallbackInfo info) {
        // if (this.inGround && blockPierceLevel > 0 && blockPierceLevel > piercedBlockPosList.size())
        // this.inGround = false;

        // if (this.piercedBlock && !((PersistentProjectileEntity) (Object) this).isInsideWall()) {
        // this.isPearcing = false;
        // this.life = 1100;
        // }

        if (!((PersistentProjectileEntity) (Object) this).world.isClient && this.isPearcing) {// blockPierceLevel > 0 &&
            // this.piercedBlock = true;
            Vec3d vec3d = ((PersistentProjectileEntity) (Object) this).getVelocity();
            // if (Math.round(vec3d.length()) >= 2.0D) {
            // Vec3d normalizeVec3d = vec3d.normalize();

            Vec3d pos = ((PersistentProjectileEntity) (Object) this).getPos();
            Vec3d futurePos = ((PersistentProjectileEntity) (Object) this).getPos().add(vec3d);

            // System.out.println(
            // ((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + pos + " : " + futurePos + " Round: " + Math.round(vec3d.length()) + " : Length: " + vec3d.length());
            // System.out.println(pos.distanceTo(futurePos));
            // if(pos.distanceTo(futurePos))

            int distance = (int) Math.round(vec3d.length()) > 0 ? (int) Math.round(vec3d.length()) : 1;
            // int futurePiercingBlockCount = ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos().isAir() ? 0 : 1;
            for (int i = 1; i <= distance; i++) {

                // System.out.println(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)) + " : " + 1.0D / distance * i + " : " + pos.lerp(futurePos, 1.0D / distance * i));
                // BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
                // ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)));
                BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
                if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(blockPos).isAir() && !this.piercedBlockPosList.contains(blockPos)) {
                    this.piercedBlockPosList.add(blockPos);

                    // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + pos + " : " + futurePos + " Round: " + Math.round(vec3d.length()) + " : Length: "
                    // + vec3d.length());

                }
                // futurePiercingBlockCount++;
                // if (futurePiercingBlockCount >= blockPierceLevel) {

                // return super.onBlockHit(blockHitResult);
                // }
            }
            // }

            if (this.piercedBlockPosList.size() >= this.blockPierceLevel + 1) {
                this.isPearcing = false;
                // System.out.println(this.piercedBlockPosList);
            } else if (this.inGround)
                this.inGround = false;
        }

        // if (this.inGround && this.isPearcing) {
        // // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());
        // this.inGround = false;
        // }
    }

    // Not called when shooting arrow
    @Inject(method = "applyEnchantmentEffects", at = @At("HEAD"), cancellable = true)
    private void applyEnchantmentEffectsMixin(LivingEntity entity, float damageModifier, CallbackInfo info) {
        this.blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, entity);
        if (this.blockPierceLevel == 0)
            this.isPearcing = false;
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

// package net.additionz.mixin;

// import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
// import org.spongepowered.asm.mixin.Unique;
// import org.spongepowered.asm.mixin.injection.At;
// import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import net.additionz.AdditionMain;
// import net.minecraft.block.BlockState;
// import net.minecraft.client.MinecraftClient;
// import net.minecraft.enchantment.EnchantmentHelper;
// import net.minecraft.entity.EntityType;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.projectile.PersistentProjectileEntity;
// import net.minecraft.nbt.NbtCompound;
// import net.minecraft.util.hit.BlockHitResult;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.math.Vec3d;
// import net.minecraft.util.math.Vec3i;
// import net.minecraft.world.World;

// @Mixin(PersistentProjectileEntity.class)
// public abstract class PersistentProjectileEntityMixin {

// @Shadow
// protected boolean inGround;
// @Shadow
// private int life;

// // @Unique
// // private List<BlockPos> piercedBlockPosList = new ArrayList<BlockPos>();
// @Unique
// private int blockPierceLevel;
// @Unique
// private boolean piercedBlock;
// @Unique
// private boolean isPearcing = true;

// @Inject(method = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;)V", at =
// @At("TAIL"))
// protected void PersistentProjectileEntityInitMixin(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, CallbackInfo info) {
// blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, owner);
// }

// @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
// private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
// nbt.putInt("BlockPierceLevel", this.blockPierceLevel);
// // nbt.putInt("PiercedBlockCount", this.piercedBlockPosList.size());
// nbt.putBoolean("PiercedBlock", this.piercedBlock);
// }

// @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
// private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
// this.blockPierceLevel = nbt.getInt("BlockPierceLevel");
// // int piercedBlockCount = nbt.getInt("PiercedBlockCount");
// // this.piercedBlockPosList = new ArrayList<BlockPos>();
// // for (int i = 0; i < piercedBlockCount; i++)
// // this.piercedBlockPosList.add(new BlockPos(0, i, 0));
// this.piercedBlock = nbt.getBoolean("PiercedBlock");

// }

// @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
// protected void onBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info) {
// // if (blockPierceLevel > 0 && (blockPierceLevel > piercedBlockPosList.size()
// // || (!piercedBlockPosList.isEmpty() && !piercedBlockPosList.get(piercedBlockPosList.size() - 1).equals(blockHitResult.getBlockPos())))) {
// // // System.out.println(piercedBlockPosList + " : " + piercedBlockPosList.size());
// // if (!piercedBlockPosList.contains(blockHitResult.getBlockPos()))
// // piercedBlockPosList.add(blockHitResult.getBlockPos());

// // info.cancel();
// // }
// // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());
// // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());

// if (blockPierceLevel > 0 && this.isPearcing) {
// this.piercedBlock = true;
// Vec3d vec3d = ((PersistentProjectileEntity) (Object) this).getVelocity();
// if (Math.round(vec3d.length()) >= 2.0D) {
// // Vec3d normalizeVec3d = vec3d.normalize();

// Vec3d pos = ((PersistentProjectileEntity) (Object) this).getPos();
// Vec3d futurePos = ((PersistentProjectileEntity) (Object) this).getPos().add(vec3d);

// System.out.println(
// ((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + pos + " : " + futurePos + " Round: " + Math.round(vec3d.length()) + " : Length: " + vec3d.length());

// int distance = (int) Math.round(vec3d.length());
// int futurePiercingBlockCount = ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos().isAir() ? 0 : 1;
// for (int i = 1; i <= distance; i++) {
// // System.out.println(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)) + " : " + 1.0D / distance * i + " : " + pos.lerp(futurePos, 1.0D / distance * i));
// // BlockPos blockPos = new BlockPos(pos.lerp(futurePos, 1.0D / distance * i));
// // ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i)));
// if (!((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(pos.lerp(futurePos, 1.0D / distance * i))).isAir())
// futurePiercingBlockCount++;
// if (futurePiercingBlockCount >= blockPierceLevel) {
// this.isPearcing = false;
// // return super.onBlockHit(blockHitResult);
// }
// }

// // for (double i = 0; i <= Math.round(1.0D / vec3d.length()); i++) {

// // System.out.println(new BlockPos(pos.lerp(futurePos, 1.0D / vec3d.length())));
// // }

// // vec3d.add(normalizeVec3d);
// // BlockState blockState = ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos();

// // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + blockState);

// // if (blockState.isAir()) {
// // vec3d.add(normalizeVec3d.normalize());
// // System.out.println("isAir: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));
// // vec3d.add(normalizeVec3d.normalize());
// // System.out.println("isAir2: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));
// // } else {
// // System.out.println("Not Air!: " + new BlockPos(vec3d) + " : " + ((PersistentProjectileEntity) (Object) this).world.getBlockState(new BlockPos(vec3d)));

// // // System.out.println(((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().add(vec3i)) + " : " + vec3i);
// // }

// // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos() + " - Time: "
// // + ((PersistentProjectileEntity) (Object) this).world.getTime());
// // // System.out.println(((PersistentProjectileEntity) (Object) this).getVelocity().);
// // System.out.println(((PersistentProjectileEntity) (Object) this).getVelocity().normalize());
// // System.out.println(((PersistentProjectileEntity) (Object) this).world
// // .getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().offset(((PersistentProjectileEntity) (Object) this).getMovementDirection().getOpposite(), 1)) + " : "
// // + ((PersistentProjectileEntity) (Object) this).getMovementDirection());
// // Vec3i vec3i = new Vec3i(Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().x),
// // Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().y), Math.round(((PersistentProjectileEntity) (Object) this).getVelocity().normalize().z));
// // System.out.println(((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().add(vec3i)) + " : " + vec3i);
// System.out.println("=====");
// // ((PersistentProjectileEntity) (Object) this).world.getBlockState(((PersistentProjectileEntity) (Object) this).getBlockPos().)
// // this.world.getBlockState(this.getBlockPos())
// if (this.isPearcing)
// info.cancel();
// }
// }
// }

// @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"))
// private void tickMixin(CallbackInfo info) {
// // if (this.inGround && blockPierceLevel > 0 && blockPierceLevel > piercedBlockPosList.size())
// // this.inGround = false;

// // if (this.piercedBlock && !((PersistentProjectileEntity) (Object) this).isInsideWall()) {
// // this.isPearcing = false;
// // this.life = 1100;
// // }

// if (this.inGround && blockPierceLevel > 0 && this.isPearcing) {
// // System.out.println(((PersistentProjectileEntity) (Object) this).getBlockPos() + " : " + ((PersistentProjectileEntity) (Object) this).getBlockStateAtPos());
// this.inGround = false;
// }
// }

// // Not called when shooting arrow
// @Inject(method = "applyEnchantmentEffects", at = @At("HEAD"), cancellable = true)
// private void applyEnchantmentEffectsMixin(LivingEntity entity, float damageModifier, CallbackInfo info) {
// blockPierceLevel = EnchantmentHelper.getEquipmentLevel(AdditionMain.BLOCK_PIERCE_ENCHANTMENT, entity);

// }

// // net/minecraft/entity/projectile/PersistentProjectileEntity.isTouchingWaterOrRain
// // ()Z
// // @ModifyVariable(method = "tick", at = @At(value = "INVOKE", target =
// // "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWaterOrRain()Z"),
// // print = true)
// // private boolean tickModifyMixin(boolean original) {
// // return original;
// // }

// // protected void onBlockHit(BlockHitResult blockHitResult) {
// // BlockState blockState =
// // this.world.getBlockState(blockHitResult.getBlockPos());
// // blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
// // }
// }
