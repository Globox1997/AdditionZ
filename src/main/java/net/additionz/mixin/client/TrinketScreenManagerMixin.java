package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.trinkets.TrinketScreenManager;
import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
@Mixin(TrinketScreenManager.class)
public class TrinketScreenManagerMixin {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true, remap = false)
    private static void updateMixin(float mouseX, float mouseY, CallbackInfo info) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement) {
            info.cancel();
        }
    }

    @Inject(method = "drawActiveGroup", at = @At("HEAD"), cancellable = true, remap = false)
    private static void drawActiveGroupMixin(DrawContext context, CallbackInfo info) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement) {
            info.cancel();
        }
    }

    @Inject(method = "drawExtraGroups", at = @At("HEAD"), cancellable = true, remap = false)
    private static void drawExtraGroupsMixin(DrawContext context, CallbackInfo info) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement) {
            info.cancel();
        }
    }
}
