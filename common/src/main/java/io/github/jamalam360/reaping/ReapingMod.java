package io.github.jamalam360.reaping;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.config.ReapingConfig;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import io.github.jamalam360.reaping.logic.ReapingToolDispenserBehavior;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Random;

public class ReapingMod {
    public static final String MOD_ID = "reaping";
    public static final String MOD_NAME = "Reaping";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(MOD_ID, Registry.CUSTOM_STAT_KEY);

    public static final Identifier USE_REAPER_TOOL_STAT = new Identifier(MOD_ID, "use_reaper_tool");

    public static final RegistrySupplier<ReaperItem> IRON_REAPER;
    public static final RegistrySupplier<ReaperItem> GOLD_REAPER;
    public static final RegistrySupplier<ReaperItem> DIAMOND_REAPER;
    public static final RegistrySupplier<ReaperItem> NETHERITE_REAPER;
    public static final RegistrySupplier<Item> HUMAN_MEAT;

    public static void init() {
        log(Level.INFO, "Initializing Reaping Mod...");

        ITEMS.register();
        STATS.register();

        AutoConfig.register(ReapingConfig.class, GsonConfigSerializer::new);

        if (Platform.isModLoaded("harvest_scythes")) {
            log(Level.INFO, "Enabling Harvest Scythe compatibility...");

            try {
                @SuppressWarnings("unchecked")
                Class<? extends Item> clazz = (Class<? extends Item>) ReapingMod.class.getClassLoader()
                        .loadClass("wraith.harvest_scythes.item.ScytheItem");
                ReapingHelper.registerValidReapingTool(clazz);
            } catch (Exception e) {
                log(Level.WARN, "Failed to enable Harvest Scythe compatibility");
            }
        }
    }

    private static Item.Settings reaperBaseProperties() {
        return new Item.Settings().group(ItemGroup.TOOLS).maxCount(1);
    }

    private static ReaperItem getReapingTool(ToolMaterials material, float sharpnessModifier) {
        ReaperItem item = new ReaperItem(reaperBaseProperties(), material, sharpnessModifier);
        DispenserBlock.registerBehavior(item, new ReapingToolDispenserBehavior());
        ReapingHelper.registerValidReapingTool(item.getClass());
        return item;
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    static {
        STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);

        IRON_REAPER = ITEMS.register(new Identifier(MOD_ID, "iron_reaping_tool"), () -> getReapingTool(ToolMaterials.IRON, 1.0F));
        GOLD_REAPER = ITEMS.register(new Identifier(MOD_ID, "gold_reaping_tool"), () -> getReapingTool(ToolMaterials.GOLD, 0.75F));
        DIAMOND_REAPER = ITEMS.register(new Identifier(MOD_ID, "diamond_reaping_tool"), () -> getReapingTool(ToolMaterials.DIAMOND, 0.4F));
        NETHERITE_REAPER = ITEMS.register(new Identifier(MOD_ID, "netherite_reaping_tool"), () -> getReapingTool(ToolMaterials.NETHERITE, 0.2F));

        HUMAN_MEAT = ITEMS.register(
                new Identifier(MOD_ID, "human_meat"),
                () -> new Item(
                        new Item.Settings()
                                .group(ItemGroup.FOOD)
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
}
