package io.github.jamalam360.reaping;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public interface CustomReapableEntity {
    ActionResult reaping$onReaped(@Nullable PlayerEntity user, ItemStack toolStack);
}
