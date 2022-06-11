package io.github.jamalam360.reaping;

import dev.architectury.platform.Platform;
import dev.architectury.registry.level.entity.trade.SimpleTrade;
import dev.architectury.registry.level.entity.trade.TradeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.jamalam360.reaping.config.ReapingConfig;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import io.github.jamalam360.reaping.logic.ReapingToolDispenserBehavior;
import io.github.jamalam360.reaping.mixin.DefaultAttributeRegistryAccessor;
import io.github.jamalam360.reaping.pillager.ReapingPillagerEntity;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
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
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(MOD_ID, Registry.ENTITY_TYPE_KEY);

    public static final RegistrySupplier<ReaperItem> IRON_REAPER;
    public static final RegistrySupplier<ReaperItem> GOLD_REAPER;
    public static final RegistrySupplier<ReaperItem> DIAMOND_REAPER;
    public static final RegistrySupplier<ReaperItem> NETHERITE_REAPER;
    public static final RegistrySupplier<Item> HUMAN_MEAT;

    public static final Identifier USE_REAPER_TOOL_STAT = new Identifier(MOD_ID, "use_reaper_tool");

    public static final RegistrySupplier<EntityType<ReapingPillagerEntity>> REAPING_PILLAGER_ENTITY_TYPE;

    public static void init() {
        log(Level.INFO, "Initializing Reaping Mod...");

        AutoConfig.register(ReapingConfig.class, GsonConfigSerializer::new);

        ITEMS.register();
        STATS.register();
        ENTITY_TYPES.register();

        DefaultAttributeRegistryAccessor.getRegistry().put(
                REAPING_PILLAGER_ENTITY_TYPE.get(),
                ReapingPillagerEntity.createReapingPillagerAttributes().build()
        );

        TradeRegistry.registerVillagerTrade(
                VillagerProfession.BUTCHER,
                2,
                new SimpleTrade(
                        new ItemStack(Items.EMERALD, 3),
                        ItemStack.EMPTY,
                        ReapingMod.IRON_REAPER.get().getDefaultStack(),
                        5,
                        10,
                        1
                )
        );

        TradeRegistry.registerVillagerTrade(
                VillagerProfession.BUTCHER,
                5,
                new SimpleTrade(
                        new ItemStack(Items.EMERALD, 13),
                        ItemStack.EMPTY,
                        ReapingMod.DIAMOND_REAPER.get().getDefaultStack(),
                        3,
                        30,
                        1
                )
        );

        TradeRegistry.registerTradeForWanderingTrader(
                true,
                new SimpleTrade(
                        new ItemStack(Items.EMERALD, 7),
                        ItemStack.EMPTY,
                        ReapingMod.HUMAN_MEAT.get().getDefaultStack(),
                        6,
                        20,
                        1
                )
        );

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

        STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);

        REAPING_PILLAGER_ENTITY_TYPE = ENTITY_TYPES.register(
                new Identifier(MOD_ID, "reaping_pillager"),
                () -> EntityType.Builder.
                        create(ReapingPillagerEntity::new, SpawnGroup.MONSTER)
                        .spawnableFarFromPlayer()
                        .setDimensions(0.6F, 1.95F)
                        .maxTrackingRange(8)
                        .build("reaping_pillager")
        );
    }
}
