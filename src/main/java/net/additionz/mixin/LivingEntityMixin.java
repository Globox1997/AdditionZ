package net.additionz.mixin;

import java.util.Iterator;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.additionz.AdditionMain;
import net.additionz.access.AttackTimeAccess;
import net.additionz.access.PassiveAgeAccess;
import net.additionz.mixin.accessor.MobEntityAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements AttackTimeAccess {

    @Shadow
    private int lastAttackedTime;
    private double oldClimbingSpeed = 0D;

    private static final Identifier PATH_BOOST_ID = Identifier.of("additionz", "path_speed");

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (!source.isIn(DamageTypeTags.IS_PROJECTILE) && amount > 1.0f && source.getSource() != null && source.getSource() instanceof LivingEntity
                && !((LivingEntity) source.getSource()).disablesShield() && AdditionMain.CONFIG.shield_blocking_cooldown != 0) {
            if ((Object) this instanceof PlayerEntity) {
                ((PlayerEntity) (Object) this).getItemCooldownManager().set(this.getActiveItem().getItem(), AdditionMain.CONFIG.shield_blocking_cooldown);
            }
            this.clearActiveItem();
        }
    }

    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;despawnCounter:I", ordinal = 0), cancellable = true)
    private void damageSpikeMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection && (source.equals(this.getDamageSources().cactus()) || source.equals(this.getDamageSources().sweetBerryBush()))
                && (Object) this instanceof LivingEntity LivingEntity
                && (LivingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET) || LivingEntity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                        || LivingEntity.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS) || LivingEntity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS))) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "applyMovementEffects", at = @At("TAIL"))
    protected void applyMovementEffectsMixin(ServerWorld world, BlockPos pos, CallbackInfo info) {
        if (AdditionMain.CONFIG.path_block_speed_boost > 0.00D) {
            EntityAttributeInstance entityAttributeInstance = ((LivingEntity) (Object) this).getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            if (entityAttributeInstance != null) {
                if (this.getWorld().getBlockState(this.getVelocityAffectingPos()).isIn(AdditionMain.PATH_BLOCKS)) {
                    if (entityAttributeInstance.getModifier(PATH_BOOST_ID) == null) {
                        entityAttributeInstance
                                .addTemporaryModifier(new EntityAttributeModifier(PATH_BOOST_ID, (double) AdditionMain.CONFIG.path_block_speed_boost, EntityAttributeModifier.Operation.ADD_VALUE));
                    }
                } else {
                    if (entityAttributeInstance.getModifier(PATH_BOOST_ID) != null) {
                        entityAttributeInstance.removeModifier(PATH_BOOST_ID);
                    }
                }
            }
        }
    }

    @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, RegistryKey<LootTable> registryKey, LootTable lootTable, LootContextParameterSet.Builder builder,
            LootContextParameterSet lootContextParameterSet) {
        if (AdditionMain.CONFIG.passive_entity_modifications && (Object) this instanceof PassiveEntity passiveEntity) {

            int realPassiveAge = (int) Math.round(Math.floor(((PassiveAgeAccess) passiveEntity).getPassiveAge() / AdditionMain.CONFIG.passiveEntityConfig.passive_age_calculation)) + 1;
            if (realPassiveAge > AdditionMain.CONFIG.passiveEntityConfig.passive_max_age) {
                realPassiveAge = AdditionMain.CONFIG.passiveEntityConfig.passive_max_age;
            }

            ObjectArrayList<ItemStack> objectArrayList = lootTable.generateLoot(lootContextParameterSet);

            float lootingChance = 0.0F;

            if (causedByPlayer && source.getSource() != null && source.getSource() instanceof LivingEntity livingEntity) {
                Optional<RegistryEntry<Enchantment>> optional = livingEntity.getMainHandStack().getEnchantments().getEnchantments().stream()
                        .filter(entry -> entry.matchesId(Enchantments.LOOTING.getRegistry())).findFirst();
                if (optional.isPresent() && !optional.isEmpty()) {
                    lootingChance = 0.15F * EnchantmentHelper.getLevel(optional.get(), livingEntity.getMainHandStack());
                }
            }

            Iterator<ItemStack> listIterator = objectArrayList.iterator();
            while (listIterator.hasNext()) {
                ItemStack itemStack = listIterator.next();
                if (itemStack.getCount() == 0) {
                    continue;
                }

                if (itemStack.get(DataComponentTypes.FOOD) != null || itemStack.isIn(AdditionMain.PASSIVE_AGE_ITEMS)) {
                    itemStack.setCount(1 + (lootingChance > 0.001F ? (this.getWorld().getRandom().nextFloat() <= lootingChance ? 1 : 0) : 0));
                }
                this.dropStack(itemStack);
            }
            info.cancel();
        }

    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathMixin(DamageSource damageSource, CallbackInfo info) {
        if ((Object) this instanceof MobEntity mobEntity && AdditionMain.ENTITY_EXPERIENCE_MAP.containsKey(mobEntity.getType())) {
            ((MobEntityAccess) mobEntity).setExperiencePoints(AdditionMain.ENTITY_EXPERIENCE_MAP.get(mobEntity.getType()));
        }
    }

    @Inject(method = "applyClimbingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isHoldingOntoLadder()Z", shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void applyClimbingSpeedMixin(Vec3d motion, CallbackInfoReturnable<Vec3d> info, float f, double d, double e, double g) {
        if (AdditionMain.CONFIG.ladder_climb_speeding && !isHoldingOntoLadder()) {
            if (this.getPitch() >= 90.0f) {
                info.setReturnValue(new Vec3d(d, g - 0.25f, e));
            }
        }

    }

    @ModifyVariable(method = "applyClimbingSpeed", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(DD)D", shift = Shift.AFTER), ordinal = 2)
    private double applyClimbingSpeedMixin(double original) {
        if (((LivingEntity) (Object) this).getWorld().isClient() && this.oldClimbingSpeed == original && AdditionMain.CONFIG.dexterity_enchantment
                && !((LivingEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
            Optional<RegistryEntry<Enchantment>> optional = ((LivingEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET).getEnchantments().getEnchantments().stream()
                    .filter(entry -> entry.matchesId(AdditionMain.DEXTERITY_ENCHANTMENT.getRegistry())).findFirst();
            if (optional.isPresent() && !optional.isEmpty()) {
                double dexterityLevel = EnchantmentHelper.getLevel(optional.get(), ((LivingEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET));
                return original > 0 ? Math.min(original * 1.5D * dexterityLevel, dexterityLevel * 0.1176D) : original * 1.3D * dexterityLevel;
            }

        }
        this.oldClimbingSpeed = original;
        return original;
    }

    @ModifyVariable(method = "tickFallFlying", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getFlag(I)Z"), ordinal = 0)
    private boolean tickFallFlyingMixin(boolean original) {
        if (AdditionMain.CONFIG.disable_elytra_underwater && this.isSubmergedInWater()) {
            return false;
        }
        return original;
    }

    @Override
    public boolean startRiding(Entity entity) {
        if (AdditionMain.CONFIG.start_riding_fall_damage) {
            this.handleFallDamage(this.fallDistance, 1.0f, this.getDamageSources().fall());
        }
        return super.startRiding(entity);
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

    @Shadow
    public boolean isHoldingOntoLadder() {
        return this.isSneaking();
    }

}
