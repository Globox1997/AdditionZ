package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.world.World;

@Mixin(StrayEntity.class)
public abstract class StrayEntityMixin extends AbstractSkeletonEntity {

    public StrayEntityMixin(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected boolean isAffectedByDaylight() {
        if (!AdditionMain.CONFIG.strays_affected_by_daylight && this.getWorld().getBiome(this.getBlockPos()).value().getTemperature() <= 0.4f) {
            return false;
        }
        return super.isAffectedByDaylight();
    }

}
