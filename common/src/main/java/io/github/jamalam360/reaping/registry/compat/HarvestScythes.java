package io.github.jamalam360.reaping.registry.compat;

import dev.architectury.platform.Platform;
import io.github.jamalam360.reaping.ReapingMod;
import io.github.jamalam360.reaping.logic.ReapingHelper;
import net.minecraft.item.Item;
import org.apache.logging.log4j.Level;

/**
 * @author Jamalam
 */
public class HarvestScythes {
    public static void tryRegister() {
        if (Platform.isModLoaded("harvest_scythes")) {
            ReapingMod.log(Level.INFO, "Enabling Harvest Scythe compatibility...");

            try {
                @SuppressWarnings("unchecked")
                Class<? extends Item> clazz = (Class<? extends Item>) ReapingMod.class.getClassLoader()
                        .loadClass("wraith.harvest_scythes.item.ScytheItem");
                ReapingHelper.registerValidReapingTool(clazz);
            } catch (Exception e) {
                ReapingMod.log(Level.WARN, "Failed to enable Harvest Scythe compatibility");
            }
        }
    }
}
