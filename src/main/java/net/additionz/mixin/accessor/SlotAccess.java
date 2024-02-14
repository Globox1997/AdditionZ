package net.additionz.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.screen.slot.Slot;

@Mixin(Slot.class)
public interface SlotAccess {

    @Accessor("x")
    public void setX(int x);

    @Accessor("y")
    public void setY(int y);

}
