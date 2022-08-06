package net.additionz.waila;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AgeWailaBlockInfo extends AgeFeature implements IEntityComponentProvider {

    public static Identifier AGE_INFO = new Identifier("additionz", "age_info");

    @Override
    public void initialize(IRegistrar registrar) {
        registrar.addConfig(AGE_INFO, true);
        registrar.addComponent(this, TooltipPosition.BODY, Block.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IEntityComponentProvider.super.appendBody(tooltip, accessor, config);
        if (config.getBoolean(AGE_INFO) && accessor.getEntity() instanceof PassiveEntity) {
            int realPassiveAge = (int) Math.round(Math.floor(((PassiveAgeAccess) (Object) this).getPassiveAge() / AdditionMain.CONFIG.passiveEntityConfig.passive_age_calculation)) + 1;
            tooltip.addLine(Text.translatable("entity.additionz.age.tooltip", ((PassiveEntity) accessor.getEntity()).isBaby() ? 0 : realPassiveAge));
        }
    }
}
