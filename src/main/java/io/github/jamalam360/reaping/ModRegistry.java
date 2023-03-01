package io.github.jamalam360.reaping;

import io.github.jamalam360.jamlib.registry.JamLibContentRegistry;
import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import io.github.jamalam360.reaping.content.enchantment.BluntnessCurseEnchantment;
import io.github.jamalam360.reaping.content.enchantment.ExecutionerEnchantment;
import io.github.jamalam360.reaping.content.ReaperItem;
import io.github.jamalam360.reaping.content.pillager.ReapingPillagerEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;

@ContentRegistry(ReapingInit.MOD_ID)
public class ModRegistry implements JamLibContentRegistry {

    public static final Enchantment CURSE_OF_BLUNTNESS = new BluntnessCurseEnchantment();
    public static final Enchantment EXECUTIONER = new ExecutionerEnchantment();

    public static final Identifier USE_REAPER_STAT = ReapingInit.idOf("use_reaper_tool");

    public static final Item IRON_REAPER = getReapingTool(ToolMaterials.IRON, 1.0F);
    public static final Item GOLD_REAPER = getReapingTool(ToolMaterials.GOLD, 0.75F);
    public static final Item DIAMOND_REAPER = getReapingTool(ToolMaterials.DIAMOND, 0.4F);
    public static final Item NETHERITE_REAPER = getReapingTool(ToolMaterials.NETHERITE, 0.2F);
    public static final Item HUMAN_MEAT = new Item(
          new Item.Settings()
                .food(new FoodComponent.Builder()
                      .meat().alwaysEdible()
                      .hunger(7).saturationModifier(1.4f)
                      .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 25 * 20), 1)
                      .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 35 * 20), 1)
                      .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 10 * 20), 1)
                      .build()
                )
    );

    public static final EntityType<ReapingPillagerEntity> REAPING_PILLAGER = EntityType.Builder.
          create(ReapingPillagerEntity::new, SpawnGroup.MONSTER)
          .spawnableFarFromPlayer()
          .setDimensions(0.6F, 1.95F)
          .maxTrackingRange(8)
          .build("reaping_pillager");

    private static ReaperItem getReapingTool(ToolMaterials material, float sharpnessModifier) {
        ReaperItem item = new ReaperItem(new Item.Settings().maxCount(1), material, sharpnessModifier);
        DispenserBlock.registerBehavior(item, new ReapingLogic.ReapingToolDispenserBehavior());
        ReapingLogic.addReapingTool(item.getClass());
        return item;
    }

    @Override
    public ItemGroup getItemGroup(Item item) {
        return item instanceof ReaperItem ? ItemGroups.COMBAT : ItemGroups.FOOD_AND_DRINK;
    }
}
