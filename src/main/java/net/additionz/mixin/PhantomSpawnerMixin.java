package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.additionz.AdditionMain;
import net.minecraft.world.spawner.PhantomSpawner;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @ModifyConstant(method = "spawn", constant = @Constant(intValue = 72000), require = 0)
    public int spawnModifyMixin(int original) {
        return AdditionMain.CONFIG.phantom_tick_time;
    }
}
