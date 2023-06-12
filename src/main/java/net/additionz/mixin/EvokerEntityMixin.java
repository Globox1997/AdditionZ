package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(EvokerEntity.class)
public abstract class EvokerEntityMixin extends SpellcastingIllagerEntity {
    private boolean usedTotem = false;

    public EvokerEntityMixin(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.usedTotem = nbt.getBoolean("UsedTotem");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("UsedTotem", this.usedTotem);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.tryUseTotem(damageSource)) {
            super.onDeath(damageSource);
        }
    }

    private boolean tryUseTotem(DamageSource source) {
        if (source.equals(this.getDamageSources().outOfWorld())) {
            return false;
        }
        if (!this.usedTotem && AdditionMain.CONFIG.evoker_use_totem_chance > 0.001F && this.getWorld().getRandom().nextFloat() <= AdditionMain.CONFIG.evoker_use_totem_chance) {
            this.usedTotem = true;
            this.setHealth(this.getMaxHealth() * 0.5F);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
            this.getWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
            return true;
        } else
            return false;
    }
}
