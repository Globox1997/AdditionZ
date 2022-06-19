package net.additionz.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class AdditionServerPacket {

    public static final Identifier STAMPEDE_DAMAGE_PACKET = new Identifier("additionz", "stampede_damage");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(STAMPEDE_DAMAGE_PACKET, (server, player, handler, buffer, sender) -> {
            if (player != null) {
                int entityId = buffer.readInt();
                int enchantmentLevel = buffer.readInt();
                player.world.getEntityById(entityId).damage(DamageSource.player(player), (float) enchantmentLevel * 2.0F);
                ((LivingEntity) player.world.getEntityById(entityId)).takeKnockback((float) enchantmentLevel * 0.5f, MathHelper.sin(player.getYaw() * ((float) Math.PI / 180)),
                        -MathHelper.cos(player.getYaw() * ((float) Math.PI / 180)));
                if (!player.isCreative())
                    player.getOffHandStack().damage(2, player, e -> e.sendEquipmentBreakStatus(EquipmentSlot.OFFHAND));

            }
        });
    }
}
