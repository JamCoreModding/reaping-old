package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.item.ReaperItem;
import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

/**
 * @author Jamalam
 */
public class ReapingEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ReapingMod.MOD_ID, Registry.ENCHANTMENT_KEY);

    public static final RegistrySupplier<Enchantment> EXECUTIONER;
    public static final RegistrySupplier<Enchantment> CURSE_OF_BLUNTNESS;

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

        CURSE_OF_BLUNTNESS = ENCHANTMENTS.register(
                ReapingMod.id("curse_of_bluntness"),
                () -> new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
                    @Override
                    public float getAttackDamage(int level, EntityGroup group) {
                        return -0.3f;
                    }

                    @Override
                    public int getMaxLevel() {
                        return 1;
                    }

                    @Override
                    public boolean canAccept(Enchantment other) {
                        return !(other instanceof DamageEnchantment) && super.canAccept(other);
                    }

                    @Override
                    public boolean isAcceptableItem(ItemStack stack) {
                        return stack.getItem() instanceof ReaperItem;
                    }

                    @Override
                    public boolean isCursed() {
                        return true;
                    }

                    @Override
                    public boolean isAvailableForRandomSelection() {
                        return true;
                    }
                }
        );
    }
}
