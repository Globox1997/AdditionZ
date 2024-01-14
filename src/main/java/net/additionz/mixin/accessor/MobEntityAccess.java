package net.additionz.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.mob.MobEntity;

@Mixin(MobEntity.class)
public interface MobEntityAccess {

    @Accessor("experiencePoints")
    public int getExperiencePoints();

    @Accessor("experiencePoints")
    public void setExperiencePoints(int experiencePoints);
}
