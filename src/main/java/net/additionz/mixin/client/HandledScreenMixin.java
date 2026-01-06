package net.additionz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Unique
    private static final Identifier SLOT_HIGHLIT = AdditionMain.identifierOf("textures/gui/slot_highlight.png");

    @Inject(method = "drawSlotHighlight", at = @At("HEAD"), cancellable = true)
    private static void drawSlotHighlightMixin(DrawContext context, int x, int y, int z, CallbackInfo info) {
        if (AdditionMain.CONFIG.change_slot_highlight) {
            context.getMatrices().push();
            RenderSystem.enableBlend();
            context.drawTexture(SLOT_HIGHLIT, x, y, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableBlend();
            context.getMatrices().pop();
            info.cancel();
        }
    }
}
