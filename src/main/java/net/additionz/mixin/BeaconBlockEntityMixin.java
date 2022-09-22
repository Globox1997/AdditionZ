package net.additionz.mixin;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity {

    private NbtList patternListNbt = null;

    public BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/BeaconBlockEntity;updateLevel(Lnet/minecraft/world/World;III)I"))
    private static void tickMixin(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo info) {
        if (!world.isClient && AdditionMain.CONFIG.beacon_shield_pattern && world.getBlockEntity(pos) != null) {
            if (world.getBlockState(pos.up()).hasBlockEntity() && world.getBlockEntity(pos.up()) instanceof BannerBlockEntity bannerBlockEntity
                    && world.getBlockState(pos.up()).getBlock() instanceof AbstractBannerBlock) {
                if (((BeaconBlockEntityMixin) (Object) blockEntity).getPatternListNbt() == null
                        || !(((BeaconBlockEntityMixin) (Object) blockEntity).getPatternListNbt().equals(BannerBlockEntity.getPatternListNbt(bannerBlockEntity.getPickStack()))))
                    ((BeaconBlockEntityMixin) (Object) blockEntity).setPatternListNbt(BannerBlockEntity.getPatternListNbt(bannerBlockEntity.getPickStack()));
            } else if (((BeaconBlockEntityMixin) (Object) blockEntity).getPatternListNbt() != null)
                ((BeaconBlockEntityMixin) (Object) blockEntity).setPatternListNbt(null);
        }
    }

    @Inject(method = "applyPlayerEffects", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void applyPlayerEffectsMixin(World world, BlockPos pos, int beaconLevel, @Nullable StatusEffect primaryEffect, @Nullable StatusEffect secondaryEffect, CallbackInfo info, double d,
            int i, int j, Box box, List<PlayerEntity> list) {
        if (AdditionMain.CONFIG.beacon_shield_pattern)
            if (world.getBlockEntity(pos) != null && ((BeaconBlockEntityMixin) (Object) world.getBlockEntity(pos)).getPatternListNbt() != null) {
                Iterator<PlayerEntity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    PlayerEntity playerEntity = iterator.next();
                    boolean hasShieldWithPattern = false;
                    ItemStack itemStack = playerEntity.getMainHandStack();

                    for (int u = 0; u < 2; u++) {
                        if (u == 1)
                            itemStack = playerEntity.getOffHandStack();
                        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ShieldItem && getItemStackPatternListNbt(itemStack) != null
                                && getItemStackPatternListNbt(itemStack).equals(((BeaconBlockEntityMixin) (Object) world.getBlockEntity(pos)).getPatternListNbt()))
                            hasShieldWithPattern = true;
                    }
                    if (!hasShieldWithPattern)
                        iterator.remove();
                }
            }
    }

    @Nullable
    private static NbtList getItemStackPatternListNbt(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains("Patterns")) {
            return null;
        }
        NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
        return nbtList;
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("PatternList"))
            setPatternListNbt(nbt.getList("PatternList", NbtElement.LIST_TYPE));
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    protected void writeNbt(NbtCompound nbt, CallbackInfo info) {
        if (getPatternListNbt() != null)
            nbt.put("PatternList", getPatternListNbt());
    }

    private void setPatternListNbt(NbtList nbtList) {
        this.patternListNbt = nbtList;
    }

    private NbtList getPatternListNbt() {
        return this.patternListNbt;
    }

}
