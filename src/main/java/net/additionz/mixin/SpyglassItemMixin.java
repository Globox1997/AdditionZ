package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpyglassItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Mixin(SpyglassItem.class)
public abstract class SpyglassItemMixin extends Item {

    public SpyglassItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);

        if (world.isClient && user instanceof PlayerEntity player && AdditionMain.CONFIG.eagle_eyed_enchantment && (player.experienceLevel > 0 || player.isCreative()) && stack.hasEnchantments()
                && EnchantmentHelper.getLevel(AdditionMain.EAGLE_EYED_ENCHANTMENT, stack) > 0) {
            HitResult hit = user.raycast(128, 0, false);
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();

            if (hit.getType() == HitResult.Type.BLOCK)
                for (int k = -1; k < 2; k++)
                    for (int i = -1; i < 2; i++)
                        for (int u = -1; u < 2; u++) {
                            BlockPos otherPos = pos.up(k).north(i).east(u);
                            if ((world.getBlockState(otherPos).getBlock() instanceof ExperienceDroppingBlock || world.getBlockState(otherPos).getBlock() instanceof RedstoneBlock
                                    || world.getBlockState(otherPos).isOf(Blocks.ANCIENT_DEBRIS)) && world.random.nextFloat() < 0.1F) {
                                for (Direction direction : Direction.values()) {
                                    ParticleUtil.spawnParticles(world, otherPos, ParticleTypes.END_ROD, UniformIntProvider.create(0, 2), direction, () -> getRandomVelocity(world.random), 0.55D);
                                }
                            }
                        }
        }
    }

    private static Vec3d getRandomVelocity(Random random) {
        return new Vec3d(MathHelper.nextDouble(random, -0.025D, 0.025D), MathHelper.nextDouble(random, -0.025D, 0.025D), MathHelper.nextDouble(random, -0.025D, 0.025D));
    }
}
