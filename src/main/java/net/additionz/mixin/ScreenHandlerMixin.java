package net.additionz.mixin;

import net.additionz.AdditionMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.trinkets.TrinketSlot;
import net.additionz.util.TrinketUtil;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Unique
    private int trinketSlotCount = 0;
    @Unique
    private int trinketSlotIdStart = -1;

    @Shadow
    @Mutable
    @Final
    public DefaultedList<Slot> slots;

    @Redirect(method = "onSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onSlotClickMixin(ScreenHandler instance, int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (actionType.equals(SlotActionType.THROW) && AdditionMain.CONFIG.trinket_slot_arrangement && slotIndex >= trinketSlotIdStart && slotIndex <= trinketSlotIdStart + trinketSlotCount) {
            actionType = SlotActionType.PICKUP;
        }
        internalOnSlotClick(slotIndex, button, actionType, player);
    }

    @Inject(method = "addSlot", at = @At("RETURN"), cancellable = true)
    private void addSlotMixin(Slot slot, CallbackInfoReturnable<Slot> info) {
        if (((ScreenHandler) (Object) this) instanceof PlayerScreenHandler && slot instanceof TrinketSlot && AdditionMain.CONFIG.trinket_slot_arrangement) {
            if (trinketSlotIdStart == -1 || trinketSlotIdStart >= slots.size() - 1) {
                trinketSlotIdStart = slots.size() - 1;
                trinketSlotCount = 0;
            }

            TrinketUtil.setTrinketSlotPosition(slot, trinketSlotCount);

            this.trinketSlotCount++;
            info.setReturnValue(slot);
        }

    }

    @Shadow
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
    }
}
