package net.additionz.waila;

import net.additionz.AdditionMain;
import net.additionz.access.PassiveAgeAccess;
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
        if (((PassiveAgeAccess) accessor.getEntity()).isImmature()) {
            tooltip.append(Text.translatable("entity.additionz.age_jade.tooltip"));
        }
    }

}
