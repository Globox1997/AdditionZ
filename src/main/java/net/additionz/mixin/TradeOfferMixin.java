package net.additionz.mixin;

import net.additionz.access.TradeOfferAccess;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TradeOffer.class)
public class TradeOfferMixin implements TradeOfferAccess {

    @Unique
    private int playerExperience = 0;

    @Override
    public void setPlayerExperience(int playerExperience) {
        this.playerExperience = playerExperience;
    }

    @Override
    public int getPlayerExperience() {
        return this.playerExperience;
    }

}
