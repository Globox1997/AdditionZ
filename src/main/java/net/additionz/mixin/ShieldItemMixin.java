package net.additionz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.additionz.network.AdditionClientPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item {

    private int stampedeCooldown = 0;

    public ShieldItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient && AdditionMain.CONFIG.stampede_enchantment && stack.hasEnchantments() && EnchantmentHelper.getLevel(AdditionMain.STAMPEDE_ENCHANTMENT, stack) > 0) {
            if (stampedeCooldown <= 0) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity) entity;
                    if (playerEntity.isBlocking() && playerEntity.isOnGround() && MinecraftClient.getInstance().options.sprintKey.isPressed()) {
                        int enchantmentLevel = EnchantmentHelper.getLevel(AdditionMain.STAMPEDE_ENCHANTMENT, stack);
                        Vec3d rotationVec3d = playerEntity.getRotationVector().multiply(1.0D, 0.1D, 1.0D).normalize();
                        rotationVec3d = rotationVec3d.multiply(1.0D + (double) enchantmentLevel * 0.5D, 1.0D, 1.0D + (double) enchantmentLevel * 0.5D);
                        playerEntity.addVelocity(rotationVec3d.x, Math.abs(rotationVec3d.y) + (double) enchantmentLevel * 0.1D, rotationVec3d.z);
                        playerEntity.getItemCooldownManager().set(stack.getItem(), 120);
                        stampedeCooldown = 120;
                    }
                }
            } else {
                if (stampedeCooldown >= 110) {
                    List<Entity> list = world.getOtherEntities(entity, entity.getBoundingBox());
                    if (!list.isEmpty())
                        for (int i = 0; i < list.size(); i++)
                            if (list.get(i) instanceof LivingEntity) {
                                AdditionClientPacket.writeC2SStampedeDamagePacket(list.get(i).getId(), EnchantmentHelper.getLevel(AdditionMain.STAMPEDE_ENCHANTMENT, stack));
                                entity.setVelocity(0.0D, 0.0D, 0.0D);
                                stampedeCooldown = 109;
                            }
                }
                stampedeCooldown--;
            }
        }
    }
}
