package net.additionz.block.screen;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.additionz.AdditionMain;
import net.additionz.block.entity.ChunkLoaderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class ChunkLoaderScreenHandler extends ScreenHandler {

    private final ScreenHandlerContext context;
    private final Inventory inventory;
    private ChunkLoaderEntity chunkLoaderEntity = null;
    private List<Integer> existingForcedChunkIds;

    public ChunkLoaderScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(6), ScreenHandlerContext.EMPTY);
        this.chunkLoaderEntity = (ChunkLoaderEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos());
        this.chunkLoaderEntity.setActive(buf.readBoolean());
        this.chunkLoaderEntity.setBurnTime(buf.readInt());
        this.chunkLoaderEntity.getChunkList().addAll(buf.readIntList());
        this.existingForcedChunkIds = Lists.newArrayList(buf.readIntList());
    }

    public ChunkLoaderScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context) {
        super(AdditionMain.CHUNK_LOADER_SCREEN_HANDLER, syncId);
        this.context = context;
        this.inventory = inventory;
        this.context.run((world, pos) -> {
            this.chunkLoaderEntity = (ChunkLoaderEntity) world.getBlockEntity(pos);
        });

        this.addSlot(new Slot(this.inventory, 0, 116, 31) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.AMETHYST_SHARD);
            }

        });
        int i;
        for (i = 1; i < this.inventory.size(); i++) {
            final int slotId = i;
            this.addSlot(new Slot(this.inventory, i, 62 + i * 18, 53) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    switch (slotId) {
                    case 1:
                        return stack.isOf(Items.IRON_BLOCK);
                    case 2:
                        return chunkLoaderEntity.getMaxChunksLoaded() == 1 && stack.isOf(Items.GOLD_BLOCK);
                    case 3:
                        return chunkLoaderEntity.getMaxChunksLoaded() == 3 && stack.isOf(Items.DIAMOND_BLOCK);
                    case 4:
                        return chunkLoaderEntity.getMaxChunksLoaded() == 5 && stack.isOf(Items.EMERALD_BLOCK);
                    case 5:
                        return chunkLoaderEntity.getMaxChunksLoaded() == 7 && stack.isOf(Items.NETHERITE_BLOCK);
                    default:
                        return false;
                    }
                }

                @Override
                public boolean canTakeItems(PlayerEntity playerEntity) {
                    if (slotId < 5 && !this.inventory.getStack(slotId + 1).isEmpty()) {
                        return false;
                    }
                    return true;
                }

                @Override
                public int getMaxItemCount() {
                    return 1;
                }

            });
        }
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
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index >= 0 && index < 6) {
                if (!this.insertItem(itemStack2, 7, 42, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 6 && index < 41) {
                if (itemStack.isOf(Items.AMETHYST_SHARD) && !this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
                // if (itemStack.isOf(Items.IRON_BLOCK)) {
                // if (!this.insertItem(itemStack2, 1, 6, false)) {
                // return ItemStack.EMPTY;
                // }
                // } else if (itemStack.isOf(Items.GOLD_BLOCK)) {
                // // if (itemStack.getItem() instanceof Gem && !this.insertGem(itemStack2, 2, 8)) {
                // // return ItemStack.EMPTY;
                // // }
                // }
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

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public ChunkLoaderEntity getChunkLoaderEntity() {
        return this.chunkLoaderEntity;
    }

    @Nullable
    public List<Integer> getExistingForcedChunkIds() {
        return this.existingForcedChunkIds;
    }

}
