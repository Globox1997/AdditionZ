package net.additionz.block.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.additionz.AdditionMain;
import net.additionz.block.screen.ChunkLoaderScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.World;

public class ChunkLoaderEntity extends BlockEntity implements Inventory, ExtendedScreenHandlerFactory {

    private UUID owner = null;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    private boolean active = false;
    private int burnTime = 0;
    private List<Integer> chunkList = new ArrayList<Integer>();

    public ChunkLoaderEntity(BlockPos pos, BlockState state) {
        super(AdditionMain.CHUNK_LOADER_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ChunkLoaderEntity blockEntity) {
        if (AdditionMain.CONFIG.chunk_loader) {
            blockEntity.tick();
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ChunkLoaderEntity blockEntity) {
        if (AdditionMain.CONFIG.chunk_loader) {
            blockEntity.tick();
        }
    }

    private void tick() {
        if (this.active) {
            if (this.burnTime > 0) {
                if (this.getMaxChunksLoaded() <= 0) {
                    this.active = false;
                    if (!this.world.isClient()) {
                        for (int i = 0; i < this.chunkList.size(); i++) {
                            updateChunkLoaderChunk((ServerWorld) world, pos, i, false);
                        }
                    }
                    return;
                } else if (this.getMaxChunksLoaded() < this.getChunkList().size()) {
                    this.getChunkList().remove(this.getChunkList().size() - 1);
                }
                if (!this.chunkList.isEmpty()) {
                    this.burnTime--;
                    if (this.burnTime == 0 && !this.inventory.get(0).isEmpty()) {
                        this.burnTime = AdditionMain.CONFIG.chunk_loader_fuel_time;
                        if (!this.world.isClient()) {
                            this.removeStack(0, 1);
                        }
                    }
                }
            } else {
                this.active = false;
                if (!this.world.isClient()) {
                    for (int i = 0; i < this.chunkList.size(); i++) {
                        updateChunkLoaderChunk((ServerWorld) world, pos, i, false);
                    }
                }
            }
        } else if (this.burnTime > 0) {
            if (!this.chunkList.isEmpty()) {
                this.active = true;
                if (!this.world.isClient()) {
                    for (int i = 0; i < this.chunkList.size(); i++) {
                        updateChunkLoaderChunk((ServerWorld) world, pos, i, true);
                    }
                }
            } else if (this.getMaxChunksLoaded() > 0) {
                this.active = true;
                this.addChunk(4);
                if (!this.world.isClient()) {
                    updateChunkLoaderChunk((ServerWorld) world, pos, 4, true);
                }
            }
        } else if (!this.inventory.get(0).isEmpty()) {
            this.burnTime = AdditionMain.CONFIG.chunk_loader_fuel_time;
            if (!this.world.isClient()) {
                this.removeStack(0, 1);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Owner")) {
            this.owner = nbt.getUuid("Owner");
        }
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.active = nbt.getBoolean("Active");
        this.burnTime = nbt.getInt("BurnTime");
        this.chunkList = Arrays.stream(nbt.getIntArray("ChunkList")).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.owner != null) {
            nbt.putUuid("Owner", this.owner);
        }
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putBoolean("Active", this.active);
        nbt.putInt("BurnTime", this.burnTime);
        nbt.putIntArray("ChunkList", this.chunkList);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ChunkLoaderScreenHandler(syncId, playerInventory, this, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.chunk_loader");
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isUpgradeEmpty() {
        for (int i = 1; i < 6; i++) {
            if (!this.inventory.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public ItemStack getUpgradeStack() {
        for (int i = 5; i > 0; i--) {
            if (!this.inventory.get(i).isEmpty()) {
                return this.inventory.get(i);
            }
        }
        return null;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        this.markDirty();
        return itemStack;
    }

    @Override
    public void markDirty() {
        world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NO_REDRAW);
        super.markDirty();
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
        buf.writeBoolean(this.active);
        buf.writeInt(this.burnTime);
        buf.writeIntList(new IntArrayList(this.chunkList));

        List<Integer> existingForcedChunkIds = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            ChunkPos chunkPos = getChunkLoaderChunkPos(pos, i);
            if (isChunkForceLoaded(player.getServerWorld(), chunkPos) && !isChunkLoadedByChunkLoader(this, chunkPos)) {
                existingForcedChunkIds.add(i);
            }
        }
        buf.writeIntList(new IntArrayList(existingForcedChunkIds));
    }

    public int getMaxChunksLoaded() {
        int chunks = 0;
        if (!this.isUpgradeEmpty()) {
            for (int i = 1; i < this.inventory.size(); i++) {
                if (!this.inventory.get(i).isEmpty()) {
                    chunks = 1 + (i - 1) * 2;
                } else {
                    break;
                }
            }
        }
        return chunks;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public List<Integer> getChunkList() {
        return this.chunkList;
    }

    public void addChunk(int chunkId) {
        if (!this.chunkList.contains(chunkId)) {
            this.chunkList.add(chunkId);
        }
    }

    public void removeChunk(int chunkId) {
        this.chunkList.remove((Object) chunkId);
    }

    public static void updateChunkLoaderChunk(ServerWorld serverWorld, BlockPos chunkLoaderBlockPos, int chunkId, boolean enableChunkLoading) {
        ChunkPos chunkPos = getChunkLoaderChunkPos(chunkLoaderBlockPos, chunkId);
        serverWorld.setChunkForced(chunkPos.x, chunkPos.z, enableChunkLoading);
    }

    public static ChunkPos getChunkLoaderChunkPos(BlockPos chunkLoaderBlockPos, int chunkId) {
        ChunkPos centerChunkpos = new ChunkPos(chunkLoaderBlockPos);
        int x = centerChunkpos.x;
        int z = centerChunkpos.z;
        switch (chunkId) {
        case 0:
            x -= 1;
            z -= 1;
            break;
        case 1:
            z -= 1;
            break;
        case 2:
            x += 1;
            z -= 1;
            break;
        case 3:
            x -= 1;
            break;
        case 4:
            break;
        case 5:
            x += 1;
            break;
        case 6:
            x -= 1;
            z += 1;
            break;
        case 7:
            z += 1;
            break;
        case 8:
            x += 1;
            z += 1;
            break;
        default:
            break;
        }
        return new ChunkPos(x, z);
    }

    // public static ChunkPos getChunkLoaderChunkPos(BlockPos chunkLoaderBlockPos, int chunkId) {
    // ChunkPos centerChunkpos = new ChunkPos(chunkLoaderBlockPos);
    // int x = centerChunkpos.x;
    // int z = centerChunkpos.z;
    // switch (chunkId) {
    // case 0:
    // x -= 1;
    // z += 1;
    // break;
    // case 1:
    // z += 1;
    // break;
    // case 2:
    // x += 1;
    // z += 1;
    // break;
    // case 3:
    // x -= 1;
    // break;
    // case 4:
    // break;
    // case 5:
    // x += 1;
    // break;
    // case 6:
    // x -= 1;
    // z -= 1;
    // break;
    // case 7:
    // z -= 1;
    // break;
    // case 8:
    // x += 1;
    // z -= 1;
    // break;
    // default:
    // break;
    // }
    // return new ChunkPos(x, z);
    // }

    public static boolean isChunkLoadedByChunkLoader(ChunkLoaderEntity chunkLoaderEntity, ChunkPos chunkPos) {
        boolean containsChunk = false;
        Iterator<Integer> iterator = chunkLoaderEntity.getChunkList().iterator();
        while (iterator.hasNext()) {
            if (chunkPos.equals(getChunkLoaderChunkPos(chunkLoaderEntity.getPos(), iterator.next()))) {
                containsChunk = true;
                break;
            }
        }
        return containsChunk;
    }

    public static boolean isChunkForceLoaded(ServerWorld world, ChunkPos chunkPos) {
        boolean forceLoaded = false;
        ForcedChunkState forcedChunkState = world.getPersistentStateManager().getOrCreate(ForcedChunkState::fromNbt, ForcedChunkState::new, "chunks");
        if (forcedChunkState.getChunks().contains(chunkPos.toLong())) {
            return true;
        }
        return forceLoaded;
    }

}
