package io.github.jamalam360.reaping;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

/**
 * @author Jamalam360
 */
public class ReaperItem extends Item implements Vanishable {
    private final ToolMaterial TOOL_MATERIAL;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public ReaperItem(Settings settings, ToolMaterial material) {
        super(settings.maxDamage(material.getDurability()));
        this.TOOL_MATERIAL = material;

        float attackDamage;
        if (material == ToolMaterials.IRON) {
            attackDamage = 4.0f;
        } else if (material == ToolMaterials.GOLD) {
            attackDamage = 5.0f;
        } else if (material == ToolMaterials.DIAMOND) {
            attackDamage = 6.0f;
        } else if (material == ToolMaterials.NETHERITE) {
            attackDamage = 7.0f;
        } else {
            attackDamage = 1.0f; // Will never happen
        }

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
        if (ReapingHelper.tryReap(entity, stack) == ActionResult.SUCCESS) {
            user.getStackInHand(hand).damage(1, user, (p) -> p.sendToolBreakStatus(hand));
            user.increaseStat(ReapingMod.USE_REAPER_TOOL_STAT, 1);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public int getEnchantability() {
        return TOOL_MATERIAL.getEnchantability();
    }
}
