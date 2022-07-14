package io.github.jamalam360.reaping.logic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import javax.annotation.Nullable;

/**
 * @author Jamalam360
 */
public interface CustomReapableEntityDuck {
    ActionResult reaping$onReaped(@Nullable PlayerEntity user, ItemStack toolStack);
}
