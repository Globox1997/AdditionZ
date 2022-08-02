package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    @Shadow
    private static TrackedData<Boolean> CHARGED;
    @Shadow
    private int explosionRadius;

    public CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt) {
        if (AdditionMain.CONFIG.charged_creeper_spawn_chance > 0.001F && world.getRandom().nextFloat() <= AdditionMain.CONFIG.charged_creeper_spawn_chance)
            this.dataTracker.set(CHARGED, true);

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void explode(CallbackInfo info, Explosion.DestructionType destructionType, float f) {
        if (AdditionMain.CONFIG.creeper_on_fire && this.isOnFire()) {
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, true, destructionType);
            this.discard();
            this.spawnEffectsCloud();
            info.cancel();
        }
    }

    @Shadow
    private void spawnEffectsCloud() {
    }

}
