package net.additionz.block;

import org.jetbrains.annotations.Nullable;

import net.additionz.AdditionMain;
import net.additionz.block.entity.ChunkLoaderEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ChunkLoaderBlock extends BlockWithEntity {

    public ChunkLoaderBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkLoaderEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getWorld().getBlockEntity(pos) != null && player.getWorld().getBlockEntity(pos) instanceof ChunkLoaderEntity chunkLoaderEntity) {
            if (player.isCreativeLevelTwoOp() || chunkLoaderEntity.getOwner() == null || chunkLoaderEntity.getOwner().equals(player.getUuid())) {
                if (!world.isClient()) {
                    ForcedChunkState forcedChunkState = ((ServerWorld) world).getPersistentStateManager().get(ForcedChunkState::fromNbt, "chunks");
                    ChunkPos chunkPos = new ChunkPos(pos);
                    if (forcedChunkState != null && forcedChunkState.getChunks().contains(chunkPos.toLong())) {
                        if (chunkLoaderEntity.getChunkList().size() <= 0 || !ChunkLoaderEntity.isChunkLoadedByChunkLoader(chunkLoaderEntity, chunkPos)) {
                            player.sendMessage(Text.translatable("block.additionz.chunk_loader.message"), true);
                            return ActionResult.FAIL;
                        }
                    }
                    player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
                }
                return ActionResult.success(world.isClient());
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock()) && world instanceof ServerWorld serverWorld && world.getBlockEntity(pos) instanceof ChunkLoaderEntity chunkLoaderEntity) {
            for (int i = 0; i < chunkLoaderEntity.getChunkList().size(); i++) {
                ChunkLoaderEntity.updateChunkLoaderChunk(serverWorld, pos, chunkLoaderEntity.getChunkList().get(i), false);
            }
            if (chunkLoaderEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) chunkLoaderEntity);
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AdditionMain.CHUNK_LOADER_ENTITY, world.isClient() ? ChunkLoaderEntity::clientTick : ChunkLoaderEntity::serverTick);
    }

}
