package io.github.jamalam360.reaping;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import io.github.jamalam360.reaping.config.ReapingConfig;
import io.github.jamalam360.reaping.entity.pillager.ReapingPillagerEntityRenderer;
import io.github.jamalam360.reaping.registry.ReapingEnchantments;
import io.github.jamalam360.reaping.registry.ReapingEntities;
import io.github.jamalam360.reaping.registry.ReapingItems;
import io.github.jamalam360.reaping.registry.ReapingStats;
import io.github.jamalam360.reaping.registry.ReapingTrades;
import io.github.jamalam360.reaping.registry.compat.HarvestScythes;
import java.util.Random;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReapingMod {

    public static final String MOD_ID = "reaping";
    public static final String MOD_NAME = "Reaping";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Random RANDOM = new Random();

    public static final CreativeTabRegistry.TabSupplier ITEM_GROUP = CreativeTabRegistry.create(
          new Identifier(MOD_ID, "tab"),
          () -> new ItemStack(ReapingItems.GOLD_REAPER.get())
    );

    public static void init() {
        log(Level.INFO, "Initializing Reaping Mod...");

        AutoConfig.register(ReapingConfig.class, GsonConfigSerializer::new);

        ReapingItems.ITEMS.register();
        ReapingEnchantments.ENCHANTMENTS.register();
        ReapingEntities.ENTITY_TYPES.register();
        ReapingEntities.registerAttributes();
        ReapingStats.STATS.register();
        ReapingTrades.registerTrades();

        HarvestScythes.tryRegister();

        EnvExecutor.runInEnv(Env.CLIENT, () -> ReapingMod.Client::initClient);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static class Client {

        public static void initClient() {
            EntityRendererRegistry.register(
                  ReapingEntities.REAPING_PILLAGER,
                  ReapingPillagerEntityRenderer::new
            );
        }
    }
}
