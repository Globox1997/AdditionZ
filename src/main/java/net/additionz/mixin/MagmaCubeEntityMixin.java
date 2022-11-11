package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.world.World;

@Mixin(MagmaCubeEntity.class)
public abstract class MagmaCubeEntityMixin extends SlimeEntity {

    public MagmaCubeEntityMixin(EntityType<? extends SlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void damage(LivingEntity target) {
        if (this.isAlive() && AdditionMain.CONFIG.magma_cube_fire_ticks != 0) {
            int i = this.getSize();
            if (this.squaredDistanceTo(target) < 0.6 * (double) i * (0.6 * (double) i))
                target.setOnFireFor(i * AdditionMain.CONFIG.magma_cube_fire_ticks);
        }
        super.damage(target);
    }

}
