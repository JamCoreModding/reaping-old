package io.github.jamalam360.reaping;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import io.github.jamalam360.reaping.registry.ReapingStats;
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

/**
 * @author Jamalam360
 */
public class ReaperItem extends Item implements Vanishable {
    public final float sharpnessModifier;
    private final ToolMaterials toolMaterial;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public ReaperItem(Settings settings, ToolMaterials material, float sharpnessModifier) {
        super(settings.maxDamage(material.getDurability()));
        this.sharpnessModifier = sharpnessModifier;
        this.toolMaterial = material;

        float attackDamage = switch (material) {
            case IRON -> 3.4f;
            case GOLD -> 4.3f;
            case DIAMOND -> 5.2f;
            case NETHERITE -> 6.8f;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + material.name());
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
        if (ReapingHelper.tryReap(user, entity, stack) == ActionResult.SUCCESS) {
            user.getStackInHand(hand).damage(1, user, (p) -> p.sendToolBreakStatus(hand));
            user.increaseStat(ReapingStats.USE_REAPER_TOOL_STAT, 1);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public int getEnchantability() {
        return toolMaterial.getEnchantability();
    }

    public int getCooldownTicks() {
        return switch (toolMaterial) {
            case IRON -> 45;
            case GOLD -> 18;
            case DIAMOND -> 28;
            case NETHERITE -> 23;
            default -> throw new IllegalArgumentException("Invalid Reaper tool material: " + toolMaterial.name());
        };
    }
}
