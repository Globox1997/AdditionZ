package net.additionz.waila;

import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum AgeJadeProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public Identifier getUid() {
        return AdditionMain.AGE_INFO;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        int realPassiveAge = (int) Math.round(Math.floor(((PassiveAgeAccess) accessor.getEntity()).getPassiveAge() / AdditionMain.CONFIG.passiveEntityConfig.passive_age_calculation)) + 1;
        tooltip.append(Text.translatable("entity.additionz.age_jade.tooltip", ((PassiveEntity) accessor.getEntity()).isBaby() ? 0 : realPassiveAge));
    }

}
