package net.additionz.mixin;

import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.additionz.access.AttackTimeAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends GolemEntity {

    private int repairedAngryTick = 0;

    public IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private int angerTime;
    @Shadow
    @Nullable
    private UUID angryAt;

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 0))
    private void initGoalsMixin(GoalSelector goalSelector, int priority, Goal goal) {
        goalSelector.add(priority, new IronGolemMeleeAttackGoal(this, 1.0, true));
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 8))
    private void initGoalsRevengeMixin(GoalSelector goalSelector, int priority, Goal goal) {
        goalSelector.add(2, new IronGolemRevengeGoal(this, new Class[0]));
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 9))
    private void initGoalsActiveTargetMixin(GoalSelector goalSelector, int priority, Goal goal) {
        goalSelector.add(3, new IronGolemActiveTargetGoal<PlayerEntity>((IronGolemEntity) (Object) this, PlayerEntity.class, 10, true, false, ((Angerable) ((Object) this))::shouldAngerAt));
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovementMixin(CallbackInfo info) {
        if (!((IronGolemEntity) (Object) this).getWorld().isClient() && this.repairedAngryTick > 0) {
            this.repairedAngryTick--;
        }
    }

    @Inject(method = "interactMob", at = @At(value = "RETURN", ordinal = 2))
    protected void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (AdditionMain.CONFIG.iron_golem_repair_friendly && getAngryAt() != null && player.getUuid().equals(getAngryAt())) {
            this.repairedAngryTick = 20;
            ((AttackTimeAccess) (LivingEntity) (Object) this).setLastAttackedTime(0);
            this.setAngerTime(0);
            this.setAngryAt(null);
            this.setTarget(null);
            this.setAttacker(null);
            this.setAttacking(false);
            this.setAttacking(null);
        }
    }

    @Shadow
    @Nullable
    public UUID getAngryAt() {
        return null;
    }

    @Shadow
    public void setAngerTime(int angerTime) {
    }

    @Shadow
    public void setAngryAt(@Nullable UUID angryAt) {
    }

    private class IronGolemMeleeAttackGoal extends MeleeAttackGoal {

        public IronGolemMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
        }

        @Override
        protected void attack(LivingEntity target, double squaredDistance) {
            if (!AdditionMain.CONFIG.iron_golem_repair_friendly) {
                super.attack(target, squaredDistance);
            }
            if (target.getY() - mob.getY() > 2.5D && target.getY() - mob.getY() < 4.5D && mob.isInRange(target, 1.5D, 4.5D) && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(target);
            } else {
                super.attack(target, squaredDistance);
            }
        }

    }

    private class IronGolemRevengeGoal extends RevengeGoal {

        public IronGolemRevengeGoal(PathAwareEntity mob, Class<?>[] noRevengeTypes) {
            super(mob, noRevengeTypes);
        }

        @Override
        public boolean shouldContinue() {
            if (((IronGolemEntityMixin) mob).repairedAngryTick > 0) {
                return false;
            }
            return super.shouldContinue();
        }

    }

    private class IronGolemActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        public IronGolemActiveTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, Predicate<LivingEntity> targetPredicate) {
            super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        }

        @Override
        public void start() {
            if (((IronGolemEntityMixin) mob).repairedAngryTick > 0) {
                this.targetEntity = null;
                this.stop();
            } else {
                super.start();
            }
        }

    }

}
