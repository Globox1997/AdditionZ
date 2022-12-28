package net.additionz.network;

import io.netty.buffer.Unpooled;
import net.additionz.AdditionMain;
import net.additionz.access.ElytraAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

public class AdditionClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(AdditionServerPacket.TOTEM_OF_NON_BREAKING_PACKET, (client, handler, buf, sender) -> {
            client.execute(() -> {
                client.particleManager.addEmitter(client.player, ParticleTypes.TOTEM_OF_UNDYING, 30);
                client.world.playSound(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.ITEM_TOTEM_USE, client.player.getSoundCategory(), 1.0f, 1.0f, false);
                client.gameRenderer.showFloatingItem(new ItemStack(AdditionMain.TOTEM_OF_NON_BREAKING));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(AdditionServerPacket.ELYTRA_DISABLING_PACKET, (client, handler, buf, sender) -> {
            int disableElytraTime = buf.readInt();
            client.execute(() -> {
                client.player.getItemCooldownManager().set(Items.ELYTRA, 100);
                ((ElytraAccess) client.player).setElytraDisablingTime(disableElytraTime);
            });
        });
    }

    public static void writeC2SStampedeDamagePacket(int entityId, int enchantmentLevel, boolean offhand) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entityId);
        buf.writeInt(enchantmentLevel);
        buf.writeBoolean(offhand);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(AdditionServerPacket.STAMPEDE_DAMAGE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    public static void writeC2SConsumeXpPacket(int amount) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(amount);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(AdditionServerPacket.CONSUME_EXPERIENCE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }
}
