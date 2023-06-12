package net.additionz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.additionz.network.AdditionClientPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    private final Identifier ORE_TEXTURE = new Identifier("additionz:textures/misc/ore_icon.png");

    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    @Unique
    private int spyglassUsage = 0;

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshairMixin(DrawContext context, CallbackInfo info) {
        if (!renderOreIcon(context) && this.spyglassUsage != 0)
            this.spyglassUsage = 0;
    }

    private boolean renderOreIcon(DrawContext context) {
        if (AdditionMain.CONFIG.eagle_eyed_enchantment && this.client.player.isUsingSpyglass() && (this.client.player.experienceLevel > 0 || this.client.player.isCreative())
                && this.client.player.getActiveItem().hasEnchantments() && EnchantmentHelper.getLevel(AdditionMain.EAGLE_EYED_ENCHANTMENT, this.client.player.getActiveItem()) > 0) {
            HitResult hit = this.client.player.raycast(128, 0, false);
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            if (hit.getType() == HitResult.Type.BLOCK)
                for (int k = -1; k < 2; k++)
                    for (int i = -1; i < 2; i++)
                        for (int u = -1; u < 2; u++) {
                            BlockPos otherPos = pos.up(k).north(i).east(u);
                            if ((this.client.world.getBlockState(otherPos).getBlock() instanceof ExperienceDroppingBlock
                                    || this.client.world.getBlockState(otherPos).getBlock() instanceof RedstoneBlock || this.client.world.getBlockState(otherPos).isOf(Blocks.ANCIENT_DEBRIS))) {
                                int scaledWidth = this.client.getWindow().getScaledWidth();
                                int scaledHeight = this.client.getWindow().getScaledHeight();
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                context.drawTexture(ORE_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);

                                if (this.spyglassUsage == 0) {
                                    AdditionClientPacket.writeC2SConsumeXpPacket(1);
                                }
                                this.spyglassUsage++;
                                return true;
                            }

                        }
        }
        return false;

    }
}
