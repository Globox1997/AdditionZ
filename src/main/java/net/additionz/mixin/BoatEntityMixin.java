package net.additionz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {

    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "fall", at = @At("HEAD"))
    protected void fallMixin(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo info) {
        if (!this.world.isClient && onGround && this.fallDistance > 4.0f && AdditionMain.CONFIG.boat_fall_damage_nerf && this.hasPassengers()) {
            List<Entity> passengers = this.getPassengerList();
            if (!passengers.isEmpty()) {
                for (int i = 0; i < passengers.size(); i++) {
                    if (passengers.get(i) instanceof LivingEntity) {
                        passengers.get(i).handleFallDamage(fallDistance, 1.0f, DamageSource.FALL);
                    }
                }
            }
        }
    }

}
