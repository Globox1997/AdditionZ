package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.additionz.access.VillagerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;

@Environment(EnvType.CLIENT)
@Mixin(VillagerResemblingModel.class)
public abstract class VillagerResemblingModelMixin implements VillagerAccess {

    @Shadow
    @Mutable
    @Final
    protected ModelPart nose;

    @Override
    public ModelPart getNose() {
        return this.nose;
    }

}
