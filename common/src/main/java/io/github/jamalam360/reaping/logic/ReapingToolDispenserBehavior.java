package io.github.jamalam360.reaping.logic;

import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
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
        if (AutoConfig.getConfigHolder(ReapingConfig.class).getConfig().enableDispenserBehavior) {
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
            return ReapingHelper.tryReap(livingEntity, stack) == ActionResult.SUCCESS;
        }

        return false;
    }
}
