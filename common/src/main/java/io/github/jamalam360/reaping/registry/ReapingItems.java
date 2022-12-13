package io.github.jamalam360.reaping.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.item.ReaperItem;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import io.github.jamalam360.reaping.logic.ReapingToolDispenserBehavior;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @author Jamalam
 */
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public class ReapingItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ReapingMod.MOD_ID, (RegistryKey<Registry<Item>>) Registries.ITEM.getKey());

    public static final RegistrySupplier<ReaperItem> IRON_REAPER;
    public static final RegistrySupplier<ReaperItem> GOLD_REAPER;
    public static final RegistrySupplier<ReaperItem> DIAMOND_REAPER;
    public static final RegistrySupplier<ReaperItem> NETHERITE_REAPER;
    public static final RegistrySupplier<Item> HUMAN_MEAT;

    static {
        IRON_REAPER = ITEMS.register(ReapingMod.id("iron_reaping_tool"), () -> getReapingTool(ToolMaterials.IRON, 1.0F));
        GOLD_REAPER = ITEMS.register(ReapingMod.id("gold_reaping_tool"), () -> getReapingTool(ToolMaterials.GOLD, 0.75F));
        DIAMOND_REAPER = ITEMS.register(ReapingMod.id("diamond_reaping_tool"), () -> getReapingTool(ToolMaterials.DIAMOND, 0.4F));
        NETHERITE_REAPER = ITEMS.register(ReapingMod.id("netherite_reaping_tool"), () -> getReapingTool(ToolMaterials.NETHERITE, 0.2F));

        HUMAN_MEAT = ITEMS.register(
              ReapingMod.id("human_meat"),
              () -> new Item(
                    new Item.Settings()
                          .arch$tab(ReapingMod.ITEM_GROUP)
                          .food(new FoodComponent.Builder()
                                .meat().alwaysEdible()
                                .hunger(7).saturationModifier(1.4f)
                                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 25 * 20), 1)
                                .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 35 * 20), 1)
                                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 10 * 20), 1)
                                .build()
                          )
              )
        );
    }

    private static Item.Settings getReaperBaseProperties() {
        return new Item.Settings().arch$tab(ReapingMod.ITEM_GROUP).maxCount(1);
    }

    private static ReaperItem getReapingTool(ToolMaterials material, float sharpnessModifier) {
        ReaperItem item = new ReaperItem(getReaperBaseProperties(), material, sharpnessModifier);
        DispenserBlock.registerBehavior(item, new ReapingToolDispenserBehavior());
        ReapingHelper.addReapingTool(item.getClass());
        return item;
    }
}
