package net.additionz.misc;

import net.additionz.AdditionMain;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public class FletchingScreenHandler extends ScreenHandler {

    private final World world;
    @Nullable
    private FletchingRecipe currentRecipe;
    // private final List<FletchingRecipe> recipes;

    protected final CraftingResultInventory output = new CraftingResultInventory();
    protected final Inventory input = new SimpleInventory(4) {

        @Override
        public void markDirty() {
            super.markDirty();
            FletchingScreenHandler.this.onContentChanged(this);
        }
    };
    protected final ScreenHandlerContext context;
    protected final PlayerEntity player;

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(AdditionMain.FLETCHING, syncId);

        this.world = playerInventory.player.world;
        // this.recipes = this.world.getRecipeManager().listAllOfType(AdditionMain.FLETCHING_RECIPE);

        int i;
        this.context = context;
        this.player = playerInventory.player;
        this.addSlot(new Slot(this.input, 0, 30, 17));
        this.addSlot(new Slot(this.input, 1, 30, 35));
        this.addSlot(new Slot(this.input, 2, 30, 53));
        this.addSlot(new Slot(this.input, 3, 73, 35));
        this.addSlot(new Slot(this.output, 4, 128, 35) {

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return FletchingScreenHandler.this.canTakeOutput(playerEntity, this.hasStack());
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                FletchingScreenHandler.this.onTakeOutput(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            this.updateResult();
        }
    }

    private boolean canTakeOutput(PlayerEntity player, boolean present) {
        return this.currentRecipe != null && this.currentRecipe.matches(this.input, this.world);
    }

    private void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.world, player, stack.getCount());
        this.output.unlockLastRecipe(player);
        for (int i = 0; i < 4; i++)
            this.decrementStack(i);
        // this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, (BlockPos)pos, 0));
    }

    private void decrementStack(int slot) {
        ItemStack itemStack = this.input.getStack(slot);
        itemStack.decrement(1);
        this.input.setStack(slot, itemStack);
    }

    private void updateResult() {
        List<FletchingRecipe> list = this.world.getRecipeManager().getAllMatches(AdditionMain.FLETCHING_RECIPE, this.input, this.world);
        if (list.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
        } else {
            boolean hasAddition = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).hasAddition()) {
                    this.currentRecipe = list.get(i);
                    hasAddition = true;
                    break;
                }
            }
            if (!hasAddition)
                this.currentRecipe = list.get(0);
            ItemStack itemStack = this.currentRecipe.craft(this.input);
            this.output.setLastRecipe(this.currentRecipe);
            this.output.setStack(0, itemStack);
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.context.get((world, pos) -> {

            if (!this.world.getBlockState((BlockPos) pos).isOf(Blocks.FLETCHING_TABLE)) {
                return false;
            }
            return player.squaredDistanceTo((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5) <= 64.0;
        }, true);
    }

    private boolean isUsableAsAddition(ItemStack stack) {
        if (stack.getItem() instanceof PotionItem)
            return true;
        return false;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 4) {
                // output slot
                if (!this.insertItem(itemStack2, 5, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index == 0 || index == 1 || index == 2 || index == 3) {
                // input slots
                if (!this.insertItem(itemStack2, 5, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 3 && index < 39) {
                // player inv slots
                int i = this.isUsableAsAddition(itemStack) ? 3 : 0;
                if (!this.insertItem(itemStack2, i, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }
}
