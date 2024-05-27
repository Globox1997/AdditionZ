package net.additionz.mixin.client;

import dev.emi.trinkets.CreativeTrinketSlot;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.TrinketScreen;
import net.additionz.AdditionMain;
import net.additionz.util.TrinketUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = CreativeInventoryScreen.class, priority = 1001)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements TrinketScreen {

    @Shadow
    private static ItemGroup selectedTab;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    protected void drawBackgroundMixin(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        TrinketUtil.renderTrinketSlotsBackground(context, this.client, this.x, this.y, selectedTab.getType() == ItemGroup.Type.INVENTORY, 6);
    }

    @ModifyVariable(method = "onMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;isCreativeInventorySlot(Lnet/minecraft/screen/slot/Slot;)Z"), ordinal = 0)
    private int onMouseClickMixin(int original, @Nullable Slot slot, int slotId, int button, SlotActionType actionType) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement && slot instanceof CreativeTrinketSlot && (actionType.equals(SlotActionType.PICKUP) || actionType.equals(SlotActionType.THROW))
                && original == -999) {
            return 0;
        }
        return original;
    }

    @ModifyVariable(method = "onMouseClick", at = @At("HEAD"), ordinal = 0)
    private SlotActionType onMouseClickMixin(SlotActionType original, @Nullable Slot slot, int slotId, int button, SlotActionType actionType) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement && slot instanceof CreativeTrinketSlot && actionType.equals(SlotActionType.THROW) && slotId == -999) {
            return SlotActionType.PICKUP;
        }
        return original;
    }

    @Inject(method = "setSelectedTab", at = @At(value = "INVOKE", target = "net/minecraft/screen/slot/Slot.<init>(Lnet/minecraft/inventory/Inventory;III)V"))
    private void setSelectedTabMixin(ItemGroup itemGroup, CallbackInfo info) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement) {
            TrinketPlayerScreenHandler handler = trinkets$getHandler();
            for (int i = handler.trinkets$getTrinketSlotStart() + (AdditionMain.isBackSlotLoaded ? -2 : 0); i < handler.trinkets$getTrinketSlotEnd() + (AdditionMain.isBackSlotLoaded ? -2 : 0); i++) {
                if (this.handler.slots.size() <= i) {
                    continue;
                }
                Slot slot = this.handler.slots.get(i);
                // if (i == handler.trinkets$getTrinketSlotEnd() - 5) {
                // System.out.println(this.handler.slots + " : " + this.handler.slots.size() + ":" + handler.trinkets$getTrinketSlotStart() + ":" + handler.trinkets$getTrinketSlotEnd());
                // }

                if (slot instanceof CreativeTrinketSlot) {
                    int slotCount = i - (handler.trinkets$getTrinketSlotStart() + (AdditionMain.isBackSlotLoaded ? -2 : 0));
                    TrinketUtil.setTrinketSlotPosition(slot, slotCount, 6);
                }
            }
        }
    }

}
