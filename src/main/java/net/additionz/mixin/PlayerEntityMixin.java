package net.additionz.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.additionz.access.ElytraAccess;
import net.additionz.network.packet.ElytraPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ElytraAccess {

    @Unique
    private int disabledElytraTimer = 0;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void createPlayerAttributesMixin(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue().add(AdditionMain.GENERIC_RANGE_ATTACK_DAMAGE);
    }

    @Inject(method = "checkFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;startFallFlying()V"), cancellable = true)
    private void checkFallFlyingMixin(CallbackInfoReturnable<Boolean> info) {
        if (this.disabledElytraTimer > 0) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private void damageElytraDisablingMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new ElytraPacket(AdditionMain.CONFIG.disable_elytra_on_damage_time));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (this.disabledElytraTimer > 0) {
            this.disabledElytraTimer--;
        }
    }

    @Override
    public void setElytraDisablingTime(int ticks) {
        this.disabledElytraTimer = ticks;
    }
}
