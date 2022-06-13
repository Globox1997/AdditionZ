package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends HostileEntity {

    @Nullable
    @Unique
    private Vec3d oldPos;

    public EndermanEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "Lnet/minecraft/entity/mob/EndermanEntity;teleportTo(DDD)Z", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void teleportToMixin(double x, double y, double z, CallbackInfoReturnable<Boolean> info, BlockPos.Mutable mutable, BlockState blockState) {

        if (info.getReturnValue() && oldPos != null && AdditionMain.CONFIG.enderman_particles) {
            Vec3d newPos = new Vec3d(mutable.getX(), mutable.getY(), mutable.getZ());

            for (int i = 0; i < (int) oldPos.distanceTo(newPos) * 10; i++) {

                double delta = 0.0D + (double) i / ((int) oldPos.distanceTo(newPos) * 28);
                Vec3d vec3d = oldPos.lerp(newPos, delta);

                // System.out.println(MathHelper.clamp(vec3d.distanceTo(newPos) / oldPos.distanceTo(newPos) * world.random.nextGaussian(), -1.0D, 1.0D));

                double maxDistance = Math.pow(vec3d.distanceTo(newPos) / oldPos.distanceTo(newPos), 5);// MathHelper.square(vec3d.distanceTo(newPos) / oldPos.distanceTo(newPos));
                // System.out
                // .println(MathHelper.clamp(maxDistance * world.random.nextGaussian() * this.getHeight() / 2.0D, -maxDistance * this.getHeight() / 2.0D, maxDistance * this.getHeight() / 2.0D));
                double particleX = vec3d.x
                        + MathHelper.clamp(maxDistance * world.random.nextGaussian() * this.getWidth() / 2.0D, -maxDistance * this.getWidth() / 2.0D, maxDistance * this.getWidth() / 2.0D);
                double particleY = vec3d.y
                        + MathHelper.clamp(maxDistance * world.random.nextGaussian() * this.getHeight() / 2.0D, -maxDistance * this.getHeight() / 2.2D, maxDistance * this.getHeight() / 2.1D);
                // - this.getHeight() / 2.0D + random.nextDouble() * this.getHeight();
                double particleZ = vec3d.z
                        + MathHelper.clamp(maxDistance * world.random.nextGaussian() * this.getWidth() / 2.0D, -maxDistance * this.getWidth() / 2.0D, maxDistance * this.getWidth() / 2.0D);

                ((ServerWorld) this.world).spawnParticles(ParticleTypes.PORTAL, particleX, particleY, particleZ, 0, 0.0D, 0.0D, 0.0D, 0.1F);
                // ((ServerWorld) this.world).spawnParticles(ParticleTypes.FLAME, vec3d.x, vec3d.y, vec3d.z, 0, 0.0D, 0.0D, 0.0D, 0.01F);
            }
        }
    }

    @Inject(method = "Lnet/minecraft/entity/mob/EndermanEntity;teleportTo(DDD)Z", at = @At("HEAD"))
    private void teleportToMixinSecond(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        oldPos = new Vec3d(this.getX(), this.getBodyY(0.5D), this.getZ());
    }

}
