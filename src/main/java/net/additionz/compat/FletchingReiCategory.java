package net.additionz.compat;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.additionz.misc.FletchingScreen;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import java.util.List;

public class FletchingReiCategory implements DisplayCategory<FlecthingReiDisplay> {

    @Override
    public CategoryIdentifier<? extends FlecthingReiDisplay> getCategoryIdentifier() {
        return AdditionzReiPlugin.FLETCHING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.additionz.fletching");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.FLETCHING_TABLE);
    }

    @Override
    public List<Widget> setupDisplay(FlecthingReiDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createTexturedWidget(FletchingScreen.TEXTURE, startPoint.x + 24, startPoint.y + 20, 0, 166, 13, 13));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 64, startPoint.y + 18)));

        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 1)).entries(display.getInputEntries().get(2)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 19)).entries(display.getInputEntries().get(1)).markInput());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 1, startPoint.y + 38)).entries(display.getInputEntries().get(0)).markInput());
        Slot slot = Widgets.createSlot(new Point(startPoint.x + 45, startPoint.y + 19)).markInput();
        if (display.getInputEntries().size() > 3) {
            slot.entries(display.getInputEntries().get(3));
        }
        widgets.add(slot);

        widgets.add(Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 66;
    }
}
