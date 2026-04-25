package net.additionz.util;

import net.additionz.AdditionMain;
import net.additionz.access.TradeOfferAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TradeUtil {

    public static void afterTradeHelper(LivingEntity livingEntity, TradeOffer tradeOffer, CallbackInfo info) {
        if (tradeOffer instanceof TradeOfferAccess tradeOfferAccess && tradeOfferAccess.getPlayerExperience() > 0) {
            livingEntity.getWorld().spawnEntity(new ExperienceOrbEntity(livingEntity.getWorld(), livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(), tradeOfferAccess.getPlayerExperience()));

            if (AdditionMain.isLevelzLoaded) {
                LevelExperienceOrbEntity.spawn((ServerWorld) livingEntity.getWorld(), livingEntity.getPos(), tradeOfferAccess.getPlayerExperience());
            }
            info.cancel();
        }
    }
}
