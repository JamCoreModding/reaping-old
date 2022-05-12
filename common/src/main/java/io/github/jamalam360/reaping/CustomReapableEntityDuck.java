package io.github.jamalam360.reaping;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

/**
 * @author Jamalam360
 */
public interface CustomReapableEntityDuck {
    ActionResult reapingmod$onReaped(ItemStack toolStack);
}
