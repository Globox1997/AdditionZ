package net.additionz.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.additionz.AdditionMain;
import net.additionz.access.TradeOfferAccess;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ExperienceUtil {

    public static void afterTradeHelper(LivingEntity livingEntity, TradeOffer tradeOffer, CallbackInfo info) {
        if (tradeOffer instanceof TradeOfferAccess tradeOfferAccess && tradeOfferAccess.getPlayerExperience() > 0) {
            livingEntity.getWorld().spawnEntity(new ExperienceOrbEntity(livingEntity.getWorld(), livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(), tradeOfferAccess.getPlayerExperience()));

            if (AdditionMain.isLevelzLoaded) {
                LevelExperienceOrbEntity.spawn((ServerWorld) livingEntity.getWorld(), livingEntity.getPos(), tradeOfferAccess.getPlayerExperience());
            }
            info.cancel();
        }
    }

    public static void breedingHelper(LivingEntity livingEntity, CallbackInfo info) {
        if (AdditionMain.BREEDING_EXPERIENCE_MAP.containsKey(livingEntity.getType())) {
            if (AdditionMain.BREEDING_EXPERIENCE_MAP.get(livingEntity.getType()) > 0) {
                livingEntity.getWorld().spawnEntity(new ExperienceOrbEntity(livingEntity.getWorld(), livingEntity.getX(), livingEntity.getY() + 0.5, livingEntity.getZ(), AdditionMain.BREEDING_EXPERIENCE_MAP.get(livingEntity.getType())));

                if (AdditionMain.isLevelzLoaded) {
                    LevelExperienceOrbEntity.spawn((ServerWorld) livingEntity.getWorld(), livingEntity.getPos(), AdditionMain.BREEDING_EXPERIENCE_MAP.get(livingEntity.getType()));
                }
            }
            info.cancel();
        }
    }

    public static boolean fishingHelper(World world, Entity entity, ItemEntity itemEntity, Operation<Boolean> original) {
        if (AdditionMain.FISHING_EXPERIENCE_MAP.containsKey(itemEntity.getStack().getItem())) {
            if (AdditionMain.FISHING_EXPERIENCE_MAP.get(itemEntity.getStack().getItem()) > 0) {
                int experience = AdditionMain.FISHING_EXPERIENCE_MAP.get(itemEntity.getStack().getItem());

                if (AdditionMain.isLevelzLoaded) {
                    LevelExperienceOrbEntity.spawn((ServerWorld) world, entity.getPos(), experience);
                }
                return world.spawnEntity(new ExperienceOrbEntity(world, entity.getX(), entity.getY() + 0.5, entity.getZ() + 0.5, experience));
            }
            return false;
        }
        return original.call(world, entity);
    }
}
