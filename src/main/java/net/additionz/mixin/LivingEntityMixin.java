package net.additionz.mixin;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.additionz.AdditionMain;
import net.additionz.access.AttackTimeAccess;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements AttackTimeAccess {

    @Shadow
    private int lastAttackedTime;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!source.isProjectile() && amount > 1.0f && source.getSource() != null && source.getSource() instanceof LivingEntity && !((LivingEntity) source.getSource()).disablesShield()
                && AdditionMain.CONFIG.shield_blocking_cooldown != 0) {
            if ((Object) this instanceof PlayerEntity)
                ((PlayerEntity) (Object) this).getItemCooldownManager().set(this.getActiveItem().getItem(), AdditionMain.CONFIG.shield_blocking_cooldown);
            this.clearActiveItem();
        }
    }

    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;despawnCounter:I", ordinal = 0), cancellable = true)
    private void damageSpikeMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection && (source.equals(DamageSource.CACTUS) || source.equals(DamageSource.SWEET_BERRY_BUSH))
                && (Object) this instanceof LivingEntity LivingEntity
                && (LivingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET) || LivingEntity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                        || LivingEntity.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS) || LivingEntity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS)))
            info.setReturnValue(false);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    protected void getVelocityMultiplierMixin(CallbackInfoReturnable<Float> info) {
        if (AdditionMain.CONFIG.path_block_speed_boost && this.world.getBlockState(this.getVelocityAffectingPos()).isIn(AdditionMain.PATH_BLOCKS))
            info.setReturnValue(1.22F);
    }

    @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContext.Builder builder) {
        if (AdditionMain.CONFIG.passive_entity_modifications && (Object) this instanceof PassiveEntity) {

            int realPassiveAge = (int) Math.round(Math.floor(((PassiveAgeAccess) (Object) this).getPassiveAge() / AdditionMain.CONFIG.passiveEntityConfig.passive_age_calculation)) + 1;
            if (realPassiveAge > AdditionMain.CONFIG.passiveEntityConfig.passive_max_age)
                realPassiveAge = AdditionMain.CONFIG.passiveEntityConfig.passive_max_age;

            ObjectArrayList<ItemStack> objectArrayList = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));

            float lootingChance = 0.0F;
            if (causedByPlayer && source.getSource() != null && source.getSource() instanceof LivingEntity && EnchantmentHelper.getLooting((LivingEntity) source.getSource()) > 0)
                lootingChance = 0.15F * EnchantmentHelper.getLooting((LivingEntity) source.getSource());

            Iterator<ItemStack> listIterator = objectArrayList.iterator();
            while (listIterator.hasNext()) {
                ItemStack itemStack = listIterator.next();
                if (itemStack.getCount() == 0)
                    continue;
                if (itemStack.isFood() || itemStack.isIn(AdditionMain.PASSIVE_AGE_ITEMS))
                    itemStack.setCount(1 + (lootingChance > 0.001F ? (this.world.random.nextFloat() <= lootingChance ? 1 : 0) : 0));

                this.dropStack(itemStack);
            }
            info.cancel();
        }

    }

    @ModifyVariable(method = "applyClimbingSpeed", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(DD)D"), ordinal = 2)
    private double applyClimbingSpeedMixin(double original) {
        if (AdditionMain.CONFIG.dexterity_enchantment && !((LivingEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET).isEmpty()
                && EnchantmentHelper.getEquipmentLevel(AdditionMain.DEXTERITY_ENCHANTMENT, (LivingEntity) (Object) this) > 0)
            return original + original * (EnchantmentHelper.getEquipmentLevel(AdditionMain.DEXTERITY_ENCHANTMENT, (LivingEntity) (Object) this) * 0.3D);

        return original;
    }

    @ModifyVariable(method = "tickFallFlying", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getFlag(I)Z"), ordinal = 0)
    private boolean tickFallFlyingMixin(boolean original) {
        if (AdditionMain.CONFIG.disable_elytra_underwater && this.isSubmergedInWater())
            return false;
        return original;
    }

    @Override
    public void setLastAttackedTime(int time) {
        this.lastAttackedTime = time;
    }

    @Shadow
    public void clearActiveItem() {
    }

    @Shadow
    public ItemStack getActiveItem() {
        return null;
    }

}
