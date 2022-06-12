package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.ReaperItem;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

/**
 * @author Jamalam
 */
public class ReapingEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ReapingMod.MOD_ID, Registry.ENCHANTMENT_KEY);

    public static final RegistrySupplier<Enchantment> EXECUTIONER;

    static {
        EXECUTIONER = ENCHANTMENTS.register(
                ReapingMod.id("executioner"),
                () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
                    @Override
                    public boolean canAccept(Enchantment other) {
                        return !(other == Enchantments.LOOTING) && super.canAccept(other);
                    }

                    @Override
                    public boolean isAcceptableItem(ItemStack stack) {
                        return stack.getItem() instanceof ReaperItem;
                    }
                }
        );
    }
}
