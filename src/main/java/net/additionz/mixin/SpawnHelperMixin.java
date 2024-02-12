package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.additionz.access.WorldAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.SpawnHelper.Checker;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {

    @Inject(method = "Lnet/minecraft/world/SpawnHelper;spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void spawnEntitiesInChunkMixin(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, SpawnHelper.Runner runner, CallbackInfo info,
            StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, int i, BlockState blockState, BlockPos.Mutable mutable, int j, int k, int l, int m, int n,
            SpawnSettings.SpawnEntry spawnEntry, EntityData entityData, int o, int p, int q, double d, double e, PlayerEntity playerEntity, double f, MobEntity mobEntity) {
        if (group.isRare() && AdditionMain.CONFIG.passive_entity_group_spawn) {
            ((WorldAccess) world).setLastSpawnBlockPos(mobEntity.getBlockPos());
        }
    }

    @Inject(method = "Lnet/minecraft/world/SpawnHelper;spawnEntitiesInChunk(Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/SpawnHelper$Checker;Lnet/minecraft/world/SpawnHelper$Runner;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SpawnHelper;canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void spawnEntitiesInChunkMixin(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, SpawnHelper.Runner runner, CallbackInfo info,
            StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, int i, BlockState blockState, BlockPos.Mutable mutable, int j, int k, int l, int m, int n,
            SpawnSettings.SpawnEntry spawnEntry, EntityData entityData, int o, int p, int q) {
        if (group.isRare() && AdditionMain.CONFIG.passive_entity_group_spawn) {
            if (((WorldAccess) world).getLastSpawnEntityType() == null || !((WorldAccess) world).getLastSpawnEntityType().equals(spawnEntry.type)) {
                ((WorldAccess) world).setLastSpawnBlockPos(null);
                ((WorldAccess) world).setLastSpawnEntityType(spawnEntry.type);
            } else if (p < spawnEntry.minGroupSize && q == o - 1 && ((WorldAccess) world).getLastSpawnBlockPos() != null) {
                MobEntity mobEntity2 = SpawnHelperMixin.createMob(world, spawnEntry.type);
                if (mobEntity2 != null) {
                    mobEntity2.initialize(world, world.getLocalDifficulty(mobEntity2.getBlockPos()), SpawnReason.NATURAL, entityData, null);
                    mobEntity2.refreshPositionAndAngles(((WorldAccess) world).getLastSpawnBlockPos(), q, p);
                    world.spawnEntityAndPassengers(mobEntity2);
                }
            }

        }
    }

    @Shadow
    private static MobEntity createMob(ServerWorld world, EntityType<?> type) {
        return null;
    }

}
