package net.additionz.network;

import io.netty.buffer.Unpooled;
import net.additionz.AdditionMain;
import net.additionz.access.ElytraAccess;
import net.additionz.block.entity.ChunkLoaderEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AdditionServerPacket {

    public static final Identifier STAMPEDE_DAMAGE_PACKET = new Identifier("additionz", "stampede_damage");
    public static final Identifier TOTEM_OF_NON_BREAKING_PACKET = new Identifier("additionz", "totem_of_non_breaking");
    public static final Identifier CONSUME_EXPERIENCE_PACKET = new Identifier("additionz", "consume_experience");
    public static final Identifier ELYTRA_DISABLING_PACKET = new Identifier("additionz", "elytra_disabling");
    public static final Identifier CHUNK_LOADER_PACKET = new Identifier("additionz", "chunk_loader");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(STAMPEDE_DAMAGE_PACKET, (server, player, handler, buffer, sender) -> {
            int entityId = buffer.readInt();
            int enchantmentLevel = buffer.readInt();
            boolean offhand = buffer.readBoolean();
            server.execute(() -> {
                player.getWorld().getEntityById(entityId).damage(player.getDamageSources().playerAttack(player), (float) enchantmentLevel * 2.0F);
                ((LivingEntity) player.getWorld().getEntityById(entityId)).takeKnockback((float) enchantmentLevel * 0.5f, MathHelper.sin(player.getYaw() * ((float) Math.PI / 180)),
                        -MathHelper.cos(player.getYaw() * ((float) Math.PI / 180)));
                if (!player.isCreative()) {
                    if (offhand) {
                        player.getOffHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));
                    } else {
                        player.getMainHandStack().damage(1, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(CONSUME_EXPERIENCE_PACKET, (server, player, handler, buffer, sender) -> {
            int amount = buffer.readInt();
            server.execute(() -> {
                if (!player.isCreative()) {
                    player.addExperience(-amount);
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(CHUNK_LOADER_PACKET, (server, player, handler, buffer, sender) -> {
            BlockPos pos = buffer.readBlockPos();
            int chunkId = buffer.readInt();
            boolean enableChunkLoading = buffer.readBoolean();
            server.execute(() -> {
                if (player.getWorld().getBlockEntity(pos) instanceof ChunkLoaderEntity chunkLoaderEntity) {
                    // Check if it is owner, also in screen too

                    if (enableChunkLoading) {
                        if (chunkLoaderEntity.getMaxChunksLoaded() > chunkLoaderEntity.getChunkList().size()
                                && !ChunkLoaderEntity.isChunkForceLoaded(player.getServerWorld(), ChunkLoaderEntity.getChunkLoaderChunkPos(pos, chunkId))) {
                            chunkLoaderEntity.addChunk(chunkId);
                            ChunkLoaderEntity.updateChunkLoaderChunk(player.getServerWorld(), pos, chunkId, enableChunkLoading);
                        }
                    } else if (ChunkLoaderEntity.isChunkLoadedByChunkLoader(chunkLoaderEntity, ChunkLoaderEntity.getChunkLoaderChunkPos(pos, chunkId))) {
                        chunkLoaderEntity.removeChunk(chunkId);
                        ChunkLoaderEntity.updateChunkLoaderChunk(player.getServerWorld(), pos, chunkId, enableChunkLoading);
                    }
                }
            });
        });
    }

    public static void writeS2CTotemOfNonBreakingPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(TOTEM_OF_NON_BREAKING_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CElytraDisablingPacket(ServerPlayerEntity serverPlayerEntity) {
        if (AdditionMain.CONFIG.disable_elytra_on_damage_time > 0) {
            serverPlayerEntity.getItemCooldownManager().set(Items.ELYTRA, AdditionMain.CONFIG.disable_elytra_on_damage_time);
            ((ElytraAccess) serverPlayerEntity).setElytraDisablingTime(AdditionMain.CONFIG.disable_elytra_on_damage_time);
            serverPlayerEntity.stopFallFlying();

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(AdditionMain.CONFIG.disable_elytra_on_damage_time);
            CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(ELYTRA_DISABLING_PACKET, buf);
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }
}
