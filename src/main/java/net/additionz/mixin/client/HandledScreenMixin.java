package net.additionz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    private static final Identifier SLOT_HIGHLIT = new Identifier("additionz", "textures/gui/slot_highlight.png");

    @Inject(method = "drawSlotHighlight", at = @At("HEAD"), cancellable = true)
    private static void drawSlotHighlightMixin(MatrixStack matrices, int x, int y, int z, CallbackInfo info) {
        if (AdditionMain.CONFIG.change_slot_highlight) {
            matrices.push();
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, SLOT_HIGHLIT);
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableBlend();
            matrices.pop();
            info.cancel();
        }
    }
}
