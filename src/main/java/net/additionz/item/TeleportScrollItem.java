package net.additionz.item;

import java.util.List;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class TeleportScrollItem extends Item {

    public TeleportScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            MinecraftClient.getInstance().getSoundManager().stopSounds(Identifier.of("minecraft:block.beacon.ambient"), null);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient() && user.hurtTime > 0) {
            user.stopUsingItem();
        }
        if (remainingUseTicks == 199 || remainingUseTicks == 125 || remainingUseTicks == 50) {
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
        if (remainingUseTicks <= 1) {
            if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayerEntity) {
                TeleportTarget teleportTarget = serverPlayerEntity.getRespawnTarget(true, TeleportTarget.NO_OP);
                serverPlayerEntity.teleport(teleportTarget.world(), teleportTarget.pos().getX(), teleportTarget.pos().getY(), teleportTarget.pos().getZ(), user.getYaw(), user.getPitch());
            } else {
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS);
            }
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        if (user instanceof PlayerEntity && !((PlayerEntity) user).isCreative()) {
            stack.decrement(1);
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.AIR);
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 200;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.additionz.teleport_scroll.tooltip"));
    }

}
