package io.github.jamalam360.reaping.mixin;

import io.github.jamalam360.reaping.item.ReaperItem;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam
 */

@SuppressWarnings({"ConstantConditions", "RedundantCast"})
@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
    @Inject(
            method = "isAcceptableItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void reaping$allowSharpnessOnReapers(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (((Enchantment) (Object) this) == Enchantments.SHARPNESS) {
            if (stack.getItem() instanceof ReaperItem) {
                cir.setReturnValue(true);
            }
        }
    }
}
