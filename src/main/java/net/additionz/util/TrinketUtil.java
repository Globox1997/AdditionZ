package net.additionz.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.additionz.AdditionMain;
import net.additionz.mixin.accessor.SlotAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class TrinketUtil {

    private static final Identifier TEXTURE = AdditionMain.identifierOf("textures/gui/trinket_slots.png");

    public static void setTrinketSlotPosition(Slot slot, int trinketSlotCount, int maxColumn) {

        int rowCount = trinketSlotCount / maxColumn;
        int rowSlotCount = trinketSlotCount % maxColumn;

        ((SlotAccess) slot).setX(-17 - rowCount * 18);
        ((SlotAccess) slot).setY(12 + rowSlotCount * 18);
    }

    public static void renderTrinketSlotsBackground(DrawContext context, MinecraftClient client, int screenX, int screenY, boolean shouldShow, int maxColumn) {
        if (AdditionMain.CONFIG.trinket_slot_arrangement && client.player != null && shouldShow) {
            Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent(client.player);
            int trinketSlotCount = 0;

            if (trinkets.isEmpty()) {
                return;
            }
            for (Map<String, TrinketInventory> map : trinkets.get().getInventory().values()) {
                trinketSlotCount += map.size();
            }

            if (trinketSlotCount > 0) {
                int rowCount = trinketSlotCount / maxColumn + (trinketSlotCount % maxColumn == 0 ? 0 : 1);
                for (int i = 0; i < rowCount; i++) {
                    int slotCount = maxColumn;
                    boolean extra = false;
                    if (rowCount == i + 1) {
                        slotCount = trinketSlotCount - maxColumn * i;
                        if (rowCount != 1 && slotCount != maxColumn) {
                            extra = true;
                        }
                    }
                    renderTrinketSidebar(context, screenX, screenY, i * 7, 0, i, slotCount, extra);
                }
            }
        }
    }

    private static void renderTrinketSidebar(DrawContext context, int screenX, int screenY, int x, int y, int row, int slotCount, boolean extra) {
        context.drawTexture(TEXTURE, screenX - 25 - row * 25 + x, screenY + 4 + y, 0, 0, 25, 7);
        int i;
        for (i = 0; i < slotCount; i++) {
            context.drawTexture(TEXTURE, screenX - 25 - row * 25 + x, screenY + 11 + i * 18 + y, 0, 7, 25, 18);
        }
        context.drawTexture(TEXTURE, screenX - 25 - row * 25 + x, screenY + 11 + i * 18 + y, 0, 25, 25, 7);
        if (extra) {
            context.drawTexture(TEXTURE, screenX - 7 - row * 25 + x, screenY + 11 + i * 18 + y, 0, 32, 7, 7);
        }
    }
}
