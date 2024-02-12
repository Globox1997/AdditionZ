package net.additionz.access;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;

public interface WorldAccess {

    public void setLastSpawnEntityType(@Nullable EntityType<?> type);

    public void setLastSpawnBlockPos(@Nullable BlockPos pos);

    @Nullable
    public EntityType<?> getLastSpawnEntityType();

    @Nullable
    public BlockPos getLastSpawnBlockPos();

}
