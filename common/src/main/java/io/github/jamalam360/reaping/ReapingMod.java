package io.github.jamalam360.reaping;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReapingMod {
    public static final String MOD_ID = "reaping";
    public static final String MOD_NAME = "Reaping";
    public static Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_KEY);
    public static final DeferredRegister<Identifier> STATS = DeferredRegister.create(MOD_ID, Registry.CUSTOM_STAT_KEY);

    public static final Identifier USE_REAPER_TOOL_STAT = new Identifier(MOD_ID, "use_reaper_tool");

    public static void init() {
        log(Level.INFO, "Initializing Reaping Mod...");

        ITEMS.register();
        STATS.register();

        if (ReapingExpectPlatform.isModLoaded("harvest_scythes")) {
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

    private static ReaperItem getReapingTool(ToolMaterial material) {
//        Registrar<Item> items = REGISTRIES.get().get(Registry.ITEM_KEY);
        ReaperItem item = new ReaperItem(reaperBaseProperties(), material);
//        items.register(new Identifier(MOD_ID, id), () -> item);
        DispenserBlock.registerBehavior(item, new ReapingToolDispenserBehavior());
        ReapingHelper.registerValidReapingTool(item.getClass());
        return item;
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    static {
        STATS.register(USE_REAPER_TOOL_STAT, () -> USE_REAPER_TOOL_STAT);

        ITEMS.register(new Identifier(MOD_ID, "iron_reaping_tool"), () -> getReapingTool(ToolMaterials.IRON));
        ITEMS.register(new Identifier(MOD_ID, "gold_reaping_tool"), () -> getReapingTool(ToolMaterials.GOLD));
        ITEMS.register(new Identifier(MOD_ID, "diamond_reaping_tool"), () -> getReapingTool(ToolMaterials.DIAMOND));
        ITEMS.register(new Identifier(MOD_ID, "netherite_reaping_tool"), () -> getReapingTool(ToolMaterials.NETHERITE));
    }
}
