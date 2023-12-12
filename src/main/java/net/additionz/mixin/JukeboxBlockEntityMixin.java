package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity {

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (nbt != null && nbt.contains("EmptyRecordItem") && nbt.getBoolean("EmptyRecordItem")) {
            setDisc(ItemStack.EMPTY);
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    protected void writeNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("EmptyRecordItem", this.getStack(0).isEmpty());
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Shadow
    public ItemStack getStack(int slot) {
        return null;
    }

    @Shadow
    public void setDisc(ItemStack stack) {
    }
}
