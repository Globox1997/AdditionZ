package net.additionz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    private final Identifier ORE_TEXTURE = new Identifier("additionz:textures/misc/ore_icon.png");

    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshairMixin(MatrixStack matrices, CallbackInfo info) {
        renderOreIcon(matrices);
    }

    private void renderOreIcon(MatrixStack matrixStack) {
        if (AdditionMain.CONFIG.eagle_eyed_enchantment && this.client.player.isUsingSpyglass() && this.client.player.getActiveItem().hasEnchantments()
                && EnchantmentHelper.getLevel(AdditionMain.EAGLE_EYED_ENCHANTMENT, this.client.player.getActiveItem()) > 0) {

            HitResult hit = this.client.player.raycast(128, 0, false);
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            if (hit.getType() == HitResult.Type.BLOCK)
                for (int k = -2; k < 3; k++)
                    for (int i = -2; i < 3; i++)
                        for (int u = -2; u < 3; u++) {
                            BlockPos otherPos = pos.up(k).north(i).east(u);
                            if ((this.client.world.getBlockState(otherPos).getBlock() instanceof OreBlock || this.client.world.getBlockState(otherPos).getBlock() instanceof RedstoneBlock)) {
                                int scaledWidth = this.client.getWindow().getScaledWidth();
                                int scaledHeight = this.client.getWindow().getScaledHeight();
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                RenderSystem.setShaderTexture(0, ORE_TEXTURE);
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                                break;
                            }

                        }
        }

    }
}
