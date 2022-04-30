package io.github.jamalam360.reaping;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

/**
 * @author Jamalam360
 */
public class ReapingToolDispenserBehavior extends FallibleItemDispenserBehavior {
    public ReapingToolDispenserBehavior() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        if (ReapingExpectPlatform.getConfig().enableDispenserBehavior()) {
            BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            this.setSuccess(tryReapEntity(pointer.getWorld(), blockPos, stack));

            if (this.isSuccess() && stack.damage(1, pointer.getWorld().random, null)) {
                stack.setCount(0);
            }
        }

        return stack;
    }

    private static boolean tryReapEntity(ServerWorld world, BlockPos pos, ItemStack stack) {
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);

        for (LivingEntity livingEntity : list) {
            if (livingEntity instanceof AnimalEntity && !livingEntity.isBaby()) {
                ReapingHelper.tryReap(livingEntity, stack);
                return true;
            }
        }

        return false;
    }
}
