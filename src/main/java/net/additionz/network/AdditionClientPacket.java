package net.additionz.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class AdditionClientPacket {

    public static void init() {
    }

    public static void writeC2SStampedeDamagePacket(int entityId, int enchantmentLevel) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeInt(enchantmentLevel);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(AdditionServerPacket.STAMPEDE_DAMAGE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }
}
