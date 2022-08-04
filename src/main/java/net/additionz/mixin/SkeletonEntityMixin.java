package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.BowItem;
import net.minecraft.world.World;

@Mixin(SkeletonEntity.class)
public abstract class SkeletonEntityMixin extends AbstractSkeletonEntity {

    public SkeletonEntityMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        if (this.getMainHandStack().getItem() instanceof BowItem) {
            if (AdditionMain.CONFIG.skeleton_bow_damaged)
                this.getMainHandStack().damage(1, this, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            if (AdditionMain.CONFIG.break_skeleton_bow_chance > 0.001F && world.getRandom().nextFloat() <= AdditionMain.CONFIG.break_skeleton_bow_chance) {
                this.getMainHandStack().damage(this.getMainHandStack().getMaxDamage(), this, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                this.updateAttackType();
            }
        }
        super.attack(target, pullProgress);
    }

}
