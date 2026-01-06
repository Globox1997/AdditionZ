package net.additionz.mixin.client;

import net.additionz.AdditionMain;
import net.additionz.network.packet.StampedePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ShieldItem.class)
public abstract class ShieldItemMixin extends Item {

    @Unique
    private int stampedeCooldown = 0;

    public ShieldItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() && AdditionMain.CONFIG.stampede_enchantment && (selected || slot == 0) && stack.hasEnchantments()
                && stack.getEnchantments().getEnchantments().stream().anyMatch(entry -> entry.matchesId(AdditionMain.STAMPEDE))) {
            if (stampedeCooldown <= 0) {
                if (entity instanceof PlayerEntity playerEntity) {
                    if (playerEntity.isBlocking() && playerEntity.isOnGround() && MinecraftClient.getInstance().options.sprintKey.isPressed()) {
                        Optional<RegistryEntry<Enchantment>> optional = stack.getEnchantments().getEnchantments().stream()
                                .filter(entry -> entry.matchesId(AdditionMain.STAMPEDE)).findFirst();
                        if (optional.isPresent() && !optional.isEmpty()) {
                            int enchantmentLevel = EnchantmentHelper.getLevel(optional.get(), stack);
                            Vec3d rotationVec3d = playerEntity.getRotationVector().multiply(1.0D, 0.1D, 1.0D).normalize();
                            rotationVec3d = rotationVec3d.multiply(1.0D + (double) enchantmentLevel * 0.5D, 1.0D, 1.0D + (double) enchantmentLevel * 0.5D);
                            playerEntity.addVelocity(rotationVec3d.x, Math.abs(rotationVec3d.y) + (double) enchantmentLevel * 0.1D, rotationVec3d.z);
                            playerEntity.getItemCooldownManager().set(stack.getItem(), 120);
                            stampedeCooldown = 120;
                        }
                    }
                }
            } else {
                if (stampedeCooldown >= 110) {
                    List<Entity> list = world.getOtherEntities(entity, entity.getBoundingBox());
                    if (!list.isEmpty()) {
                        for (Entity value : list)
                            if (value instanceof LivingEntity) {
                                Optional<RegistryEntry<Enchantment>> optional = stack.getEnchantments().getEnchantments().stream()
                                        .filter(entry -> entry.matchesId(AdditionMain.STAMPEDE)).findFirst();
                                if (optional.isPresent() && !optional.isEmpty()) {
                                    int enchantmentLevel = EnchantmentHelper.getLevel(optional.get(), stack);
                                    ClientPlayNetworking.send(new StampedePacket(value.getId(), enchantmentLevel, slot == 0));
                                    entity.setVelocity(0.0D, 0.0D, 0.0D);
                                    stampedeCooldown = 109;
                                }
                            }
                        world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
                    }
                }
                stampedeCooldown--;
            }
        }
    }
}
