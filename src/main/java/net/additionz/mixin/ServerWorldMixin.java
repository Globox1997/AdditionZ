package net.additionz.mixin;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.additionz.access.WorldAccess;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements WorldAccess {

    @Nullable
    @Unique
    private EntityType<?> lastSpawnedEntityType = null;

    @Nullable
    @Unique
    private BlockPos lastSpawnedBlockPos = null;

    public ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
            Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;canSetSnow(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void tickChunkMixin(WorldChunk chunk, int randomTickSpeed, CallbackInfo info, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, BlockPos blockPos2,
            Biome biome) {
        if (AdditionMain.CONFIG.snow_under_trees && !biome.doesNotSnow(blockPos)) {
            BlockPos underLeavesBlockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.getRandomPosInChunk(i, 0, j, 15));
            if (this.random.nextInt(3) == 0 && biome.canSetSnow(this, underLeavesBlockPos)) {
                this.setBlockState(underLeavesBlockPos, Blocks.SNOW.getDefaultState());
            }
            if (this.getBlockState(blockPos2).isIn(BlockTags.LEAVES)) {
                for (int u = 2; u < 14; u++) {
                    if (this.getBlockState(blockPos.down(u)).isSolidBlock(this, blockPos.down(u)) && !this.getBlockState(blockPos.down(u)).isIn(BlockTags.LEAVES)) {
                        break;
                    }
                    if (this.getBlockState(blockPos.down(u)).isAir() && this.getBlockState(blockPos.down(u + 1)).isIn(BlockTags.LEAVES) && biome.canSetSnow(this, blockPos.down(u))) {
                        this.setBlockState(blockPos.down(u), Blocks.SNOW.getDefaultState());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setLastSpawnEntityType(EntityType<?> type) {
        this.lastSpawnedEntityType = type;
    }

    @Override
    public void setLastSpawnBlockPos(@Nullable BlockPos pos) {
        this.lastSpawnedBlockPos = pos;
    }

    @Override
    public @Nullable EntityType<?> getLastSpawnEntityType() {
        return this.lastSpawnedEntityType;
    }

    @Override
    public @Nullable BlockPos getLastSpawnBlockPos() {
        return this.lastSpawnedBlockPos;
    }
}
