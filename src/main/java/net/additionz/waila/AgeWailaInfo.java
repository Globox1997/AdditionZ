package net.additionz.waila;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
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
        if (config.getBoolean(AdditionMain.AGE_INFO)) {
            int realPassiveAge = (int) Math.round(Math.floor(((PassiveAgeAccess) accessor.getEntity()).getPassiveAge() / AdditionMain.CONFIG.passiveEntityConfig.passive_age_calculation)) + 1;
            tooltip.addLine(Text.translatable("entity.additionz.age.tooltip", ((PassiveEntity) accessor.getEntity()).isBaby() ? 0 : realPassiveAge));
        }
    }
}
