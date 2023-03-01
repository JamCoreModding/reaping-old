/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.reaping.content;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.jamalam360.reaping.ModRegistry;
import io.github.jamalam360.reaping.ReapingLogic;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.Vanishable;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ReaperItem extends Item implements Vanishable {
    public final float sharpnessModifier;
    private final ToolMaterials toolMaterial;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public ReaperItem(Settings settings, ToolMaterials material, float sharpnessModifier) {
        super(settings.maxDamage(material.getDurability()));
        this.sharpnessModifier = sharpnessModifier;
        this.toolMaterial = material;

        float attackDamage = switch (this.toolMaterial) {
            case IRON -> 3.4f;
            case GOLD -> 4.3f;
            case DIAMOND -> 5.2f;
            case NETHERITE -> 6.8f;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + this.toolMaterial.name());
        };

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", -2.8f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (ReapingLogic.tryReap(user, entity, stack) == ActionResult.SUCCESS) {
            user.getStackInHand(hand).damage(1, user, (p) -> p.sendToolBreakStatus(hand));
            user.increaseStat(ModRegistry.USE_REAPER_STAT, 1);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public int getEnchantability() {
        return this.toolMaterial.getEnchantability();
    }

    public int getCooldownTicks() {
        return switch (this.toolMaterial) {
            case IRON -> 45;
            case GOLD -> 18;
            case DIAMOND -> 28;
            case NETHERITE -> 23;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + toolMaterial.name());
        };
    }
}
