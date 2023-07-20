package net.additionz.mixin.client;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

import com.mojang.blaze3d.systems.RenderSystem;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    private static final Identifier POLAR_STAR = new Identifier("additionz", "textures/environment/star.png");
    private static final Identifier BLUE_STAR = new Identifier("additionz", "textures/environment/blue_star.png");
    private static final Identifier RED_STAR = new Identifier("additionz", "textures/environment/red_star.png");
    private static final Identifier GREEN_STAR = new Identifier("additionz", "textures/environment/green_star.png");

    @Shadow
    @Mutable
    @Final
    private MinecraftClient client;

    @Inject(method = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderSkyMixin(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info,
            CameraSubmersionType cameraSubmersionType, Vec3d vec3d, float f, float g, float h, BufferBuilder bufferBuilder) {

        if (AdditionMain.CONFIG.polar_star) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            Matrix4f matrix4fStar = matrices.peek().getPositionMatrix();
            RenderSystem.setShaderTexture(0, POLAR_STAR);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            int k = 1; // size
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, -k).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, k).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, k).texture(0.0f, 1.0f).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

        if (AdditionMain.CONFIG.other_stars && this.client.player != null && this.client.player.isUsingSpyglass()) {
            matrices.push();
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * j));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * j));
            Matrix4f matrix4fStar = matrices.peek().getPositionMatrix();
            RenderSystem.setShaderTexture(0, BLUE_STAR);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            int k = 1; // size
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, -k).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, k).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, k).texture(0.0f, 1.0f).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * j));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * j + 90.0f));
            matrix4fStar = matrices.peek().getPositionMatrix();
            RenderSystem.setShaderTexture(0, RED_STAR);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            k = 2; // size
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, -k).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, k).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, k).texture(0.0f, 1.0f).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * j + 45.0f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * j + 180.0f));
            matrix4fStar = matrices.peek().getPositionMatrix();
            RenderSystem.setShaderTexture(0, GREEN_STAR);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            k = 1; // size
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, -k).texture(0.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, -k).texture(1.0f, 0.0f).next();
            bufferBuilder.vertex(matrix4fStar, k, 100.0f, k).texture(1.0f, 1.0f).next();
            bufferBuilder.vertex(matrix4fStar, -k, 100.0f, k).texture(0.0f, 1.0f).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            matrices.pop();
        }

    }
}
