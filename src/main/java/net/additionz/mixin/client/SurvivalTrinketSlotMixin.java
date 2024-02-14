package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.trinkets.SurvivalTrinketSlot;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(SurvivalTrinketSlot.class)
public class SurvivalTrinketSlotMixin {

    @Shadow(remap = false)
    @Mutable
    @Final
    private boolean alwaysVisible;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void initMixin(TrinketInventory inventory, int index, int x, int y, SlotGroup group, SlotType type, int slotOffset, boolean alwaysVisible, CallbackInfo info) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement) {
            this.alwaysVisible = true;
        }
    }

}
