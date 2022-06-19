package net.additionz.network;

import io.netty.buffer.Unpooled;
import net.additionz.AdditionMain;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class AdditionClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(AdditionServerPacket.TOTEM_OF_NON_BREAKING_PACKET, (client, handler, buf, sender) -> {
            if (client.player != null) {
                client.particleManager.addEmitter(client.player, ParticleTypes.TOTEM_OF_UNDYING, 30);
                client.world.playSound(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.ITEM_TOTEM_USE, client.player.getSoundCategory(), 1.0f, 1.0f, false);
                client.gameRenderer.showFloatingItem(new ItemStack(AdditionMain.TOTEM_OF_NON_BREAKING));
            }
        });
    }

    public static void writeC2SStampedeDamagePacket(int entityId, int enchantmentLevel) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeInt(enchantmentLevel);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(AdditionServerPacket.STAMPEDE_DAMAGE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }
}
