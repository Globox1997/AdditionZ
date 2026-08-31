package net.additionz.waila;

import mcp.mobius.waila.api.*;
import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.Text;

public class AgeWailaInfo extends AgeFeature implements IEntityComponentProvider {

    @Override
    public void initialize(IRegistrar registrar) {
        registrar.addConfig(AdditionMain.AGE_INFO, true);
        registrar.addComponent(this, TooltipPosition.BODY, PassiveEntity.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IEntityComponentProvider.super.appendBody(tooltip, accessor, config);
        if (config.getBoolean(AdditionMain.AGE_INFO) && ((PassiveAgeAccess) accessor.getEntity()).isImmature()) {
            tooltip.addLine(Text.translatable("entity.additionz.age.tooltip"));
        }
    }
}
